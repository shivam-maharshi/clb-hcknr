import datetime
import h5py
import pycurl
import sys, os
import time
import xmltodict

from socket import error as SocketError
from subprocess import call
from StringIO import StringIO


# create fedora object
def create_fedora_object(rdf_data, fedora_url, filename):
    storage = StringIO()
    c = pycurl.Curl()
    c.setopt(c.URL, "{}/{}".format(fedora_url, filename[:-3]))
    c.setopt(pycurl.CUSTOMREQUEST, "PUT")
    c.setopt(pycurl.HTTPHEADER, ["Content-type: text/turtle"])
    c.setopt(c.POSTFIELDS, rdf_data)
    c.setopt(c.WRITEFUNCTION, storage.write)
    c.perform()
    c.close()
    content = storage.getvalue()

    return content


# create fedora binary
def create_fedora_binary(file_path, fedora_url):
    storage = StringIO()
    f = open(file_path, "rb")
    fs = os.path.getsize(file_path)
    c = pycurl.Curl()
    c.setopt(c.URL, fedora_url)
    c.setopt(c.PUT, 1)
    c.setopt(c.READDATA, f)
    c.setopt(c.INFILESIZE, int(fs))
    c.setopt(pycurl.HTTPHEADER, ["Content-type: text/xml"])
    c.setopt(c.WRITEFUNCTION, storage.write)
    c.perform()
    c.close()
    content = storage.getvalue()

    return content


def run(fedora_url, remote_file_downloader, work_item_client, results_destination=None):
    output_file = open(
        os.path.join(results_destination if (results_destination and os.path.exists(results_destination)) else ".",
                     "experiment1_{}_results.csv".format(datetime.date.today())), "a")
    url_file = open("fedoraurls.txt", "a")

    progress = []

    start = str(datetime.datetime.now())
    tic = time.time()

    while True:
        # obtain work item from work_item_client (see commons.py for implementations)
        work_item = work_item_client.get_work_item()
        if not work_item:
            break
        file_name = work_item.strip()
        actual_file_name = "2015" + file_name[4:]

        # download remote file from remote storage (see commons.py for implementations)
        download = time.time()
        remote_file_downloader.download_from_storage(actual_file_name, file_name)
        progress.append("Download," + file_name + "," + str(download) + "," + str(time.time()))

        # read hdf5 file
        f = h5py.File(file_name, 'r')

        processing = time.time()
        if f.keys()[0] is not None:
            data_sets = f[f.keys()[0]]
            channel_str = ""
            c = 0
            for channel in data_sets.keys():
                if c == len(data_sets.keys()) - 1:
                    channel_str = channel_str + '<> dc:coverage "' + channel + '" '
                else:
                    channel_str = channel_str + '<> dc:coverage "' + channel + '" . '
                c += 1

            # run fits program
            call("fits-0.9.0/fits.sh -i " + file_name + " > " + file_name + "_fits.xml", shell=True)

            # read fits xml
            fits_xml = open(file_name + "_fits.xml", 'r').read()
            result = xmltodict.parse(fits_xml)
            description = result['fits']['identification']['identity'][0]['@format']
            format = result['fits']['identification']['identity'][0]['@mimetype']

            fits_str = '<> dc:description "' + description + '" . ' + '<> dc:format "' + format + '" . '

            # create Fedora object
            rdf_data = 'PREFIX dc: <http://purl.org/dc/elements/1.1/> <> dc:title "' + file_name + '" . ' + \
                       fits_str + channel_str
            progress.append("Processing," + file_name + "," + str(processing) + "," + str(time.time()))

            ingestion = time.time()
            object_url = ""
            for x in xrange(0, 5):
                try:
                    object_url = create_fedora_object(rdf_data, fedora_url, file_name)
                    break
                except SocketError as e:
                    if x == 4:
                        raise SocketError("retry 5 times still failed in fedora" + str(e.errno))
                    pass

            # create Fedora binary
            if len(object_url) > 0:
                file_url = object_url + "/h5"
                create_fedora_binary(file_name, file_url)
                progress.append("Ingestion," + file_name + "," + str(ingestion) + "," + str(time.time()))
                url_file.write(object_url + "\n")
                print file_url

        f.close()
        os.remove(file_name)
        os.remove(file_name + "_fits.xml")

    duration = str(time.time() - tic)
    end = str(datetime.datetime.now())
    print duration
    progress.insert(0, "OVERALL EXECUTION," + start + "," + duration + "," + end)
    for line in progress:
        output_file.write(line + "\n")
    output_file.close()
    url_file.close()


if __name__ == "__main__":
    fedoraurl = sys.argv[1]
    google_drive_dir = sys.argv[2]
    data_set_filename = sys.argv[3]

    from commons import GoogleDriveDownloader, FileSystemClient

    run(fedoraurl, GoogleDriveDownloader(google_drive_dir), FileSystemClient(data_set_filename))
