from subprocess import call


class RemoteFileDownloader(object):
    def __init__(self):
        super(RemoteFileDownloader, self).__init__()

    def download_from_storage(self, filename, destination):
        raise NotImplementedError


class GoogleDriveDownloader(RemoteFileDownloader):
    def __init__(self, google_drive_dir):
        super(GoogleDriveDownloader, self).__init__()
        self.url = "https://googledrive.com/host/" + google_drive_dir + "/{}"

    def download_from_storage(self, filename, destination):
        call("wget -nv " + self.url.format(filename) + " -O " + destination, shell=True)


class S3Downloader(RemoteFileDownloader):
    def __init__(self, s3_bucket):
        super(S3Downloader, self).__init__()
        self.url = "https://s3.amazonaws.com/" + s3_bucket + "/{}"

    def download_from_storage(self, filename, destination):
        call("wget -nv " + self.url.format(filename) + " -O " + destination, shell=True)


class WorkItemClient(object):
    def __init__(self):
        super(WorkItemClient, self).__init__()

    def get_work_item(self):
        raise NotImplementedError


class FileSystemClient(WorkItemClient):
    def __init__(self, input_file):
        super(FileSystemClient, self).__init__()
        with open(input_file) as f:
            self.lines = f.readlines()
        self.current = 0

    def get_work_item(self):
        while self.current < len(self.lines):
            line = self.lines[self.current]
            self.current += 1
            return line


class RabbitMQClient(WorkItemClient):
    def __init__(self, connection, queue_name):
        super(RabbitMQClient, self).__init__()
        self.queue_name = queue_name
        self.connection = connection
        self.channel = self.connection.channel()
        self.delivery_tag = None
        self.channel_open = True

    def get_work_item(self):
        if self.delivery_tag:
            self.channel.basic_ack(self.delivery_tag)
        while self.channel_open:
            method_frame, header_frame, body = self.channel.basic_get(self.queue_name)
            if method_frame:
                self.delivery_tag = method_frame.delivery_tag
                if not body:
                    self._disconnect()
                return body

    def _disconnect(self):
        self.channel.basic_ack(self.delivery_tag)
        self.channel.close()
        self.delivery_tag = None
        self.channel_open = False
