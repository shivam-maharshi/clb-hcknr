import datetime
import json
import pycurl
import requests
import sys, os
import time

from subprocess import call
from StringIO import StringIO


def read_fedora_object(fedora_url):
    storage = StringIO()
    c = pycurl.Curl()
    c.setopt(c.URL, fedora_url)
    c.setopt(pycurl.HTTPHEADER, ["Accept: application/ld+json"])
    c.setopt(c.WRITEFUNCTION, storage.write)
    c.perform()
    c.close()
    content = storage.getvalue()

    return content


def update_fedora_binary(update_str, fedora_url):
    binary_res = requests.patch(url=fedora_url,
                                data=update_str,
                                headers={'Content-Type': 'application/sparql-update'})

    return binary_res.text


def get_fedora_sha(input):
    json_data = json.loads(input)
    sha_data = json_data[0]['http://www.loc.gov/premis/rdf/v1#hasMessageDigest'][0]['@id']

    return sha_data.replace('urn:sha1:', "")


def sha1_of_file(file_path):
    import hashlib
    with open(file_path, 'rb') as f:
        return hashlib.sha1(f.read()).hexdigest()


def run(work_item_client, results_destination=None):
    output_file = open(
        os.path.join(results_destination if (results_destination and os.path.exists(results_destination)) else ".",
                     "experiment2_{}_results.csv".format(datetime.date.today())), "a")

    progress = []

    start = str(datetime.datetime.now())
    tic = time.time()

    file_name = "temp.h5"
    while True:
        # obtain work item from work_item_client (see commons.py for implementations)
        work_item = work_item_client.get_work_item()
        if not work_item:
            break
        fedora_obj_url = work_item.strip()
        fedora_h5_url = fedora_obj_url + "/h5"

        # read fedora object
        processing = time.time()
        content = read_fedora_object(fedora_h5_url + "/fcr:metadata")

        # read fedora sha
        fedora_sha = get_fedora_sha(content)

        # download h5 file
        download = time.time()
        call("wget -nv " + fedora_h5_url + " -O " + file_name, shell=True)
        download_elapsed = time.time() - download
        progress.append("Download," + fedora_obj_url + "," + str(download) + "," + str(download + download_elapsed))

        # create sha-1
        file_sha = sha1_of_file(file_name)

        # compare sha-1 and update fedora object
        if fedora_sha == file_sha:
            sha_result = "digest check passed at " + datetime.datetime.utcnow().isoformat() + 'Z'
        else:
            sha_result = "digest check failed at " + datetime.datetime.utcnow().isoformat() + 'Z'

        update_str = "PREFIX dc: <http://purl.org/dc/elements/1.1/> INSERT { <> dc:provenance \"" + sha_result + "\" . } WHERE { } "
        progress.append(
            "Processing," + fedora_obj_url + "," + str(processing) + "," + str(time.time() - download_elapsed))

        ingestion = time.time()
        update_fedora_binary(update_str, fedora_obj_url)
        progress.append("Ingestion," + fedora_obj_url + "," + str(ingestion) + "," + str(time.time()))
        os.remove(file_name)

    duration = str(time.time() - tic)
    end = str(datetime.datetime.now())
    print duration
    progress.insert(0, "OVERALL EXECUTION," + start + "," + duration + "," + end)
    for line in progress:
        output_file.write(line + "\n")
    output_file.close()


if __name__ == "__main__":
    fedora_urls_filename = sys.argv[1]

    from commons import FileSystemClient

    run(FileSystemClient(fedora_urls_filename))
