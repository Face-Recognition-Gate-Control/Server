import struct
import socket
import time
import os
import json
import abc
from typing import List, Type, Callable, NewType
from io import BufferedReader
import mimetypes


HOST = 'localhost'    # The remote host
PORT = 9876         # The same port as used by the server

# TOTAL PAYLOAD SIZE
PAYLOAD_LENGTH = 4

# SIZE OF THE PAYLOAD IDENTIFIER
PAYLOAD_NAME_LENGTH = 4

# SIZE OF SEGMENTS HEADER
SEGMENTS_LENGTH = 4

# SIZE OF JSON PAYLOAD
JSON_LENGTH = 4

ENDIANES = "big"

ENCODING = "UTF-8"

# Mimics Python socket.sendall signature
SendFunction = NewType('SendFunction', Callable[[bytes, int], None])
RecieveFunction = NewType('RecieveFunction', Callable[[bytes, int], None])

# RESPONSIBLE FOR CONNECTION TO SERVER


class FractalClient:

    def __init__(self, host: str, port: int):
        pass
        self.socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        self.socket.connect((host, port))

    def getSendAllFunction(self):
        return SendFunction(self.socket.sendall)

    def getReciever(self):
        return self.socket.recv
        pass


# CONNECT TO SERVER
client = FractalClient(HOST, PORT)

reciever = client.getReciever()
UserId = NewType('UserId', int)


class StreamHelper:

    byteOrder = "big"  # big or little

    @staticmethod
    def readByte(reader: RecieveFunction, signed: bool = False):
        return int.from_bytes(
            reader(1), byteorder=StreamHelper.byteOrder, signed=signed)

    @staticmethod
    def readShort(reader: RecieveFunction, signed: bool = False):
        return int.from_bytes(
            reader(2), byteorder=StreamHelper.byteOrder, signed=signed)
        pass

    @staticmethod
    def readInt(reader: RecieveFunction, signed: bool = False):
        return int.from_bytes(
            reader(4), byteorder=StreamHelper.byteOrder, signed=signed)
        pass

    @staticmethod
    def readLong(reader: RecieveFunction, signed: bool = False):
        return int.from_bytes(
            reader(8), byteorder=StreamHelper.byteOrder, signed=signed)
        pass

    @staticmethod
    def readFloat(reader: RecieveFunction):
        struct.unpack('f', reader(4))

    @staticmethod
    def readDouble(reader: RecieveFunction):
        struct.unpack('f', reader(8))

    @staticmethod
    def readBytesToString(reader: RecieveFunction, nBytes):
        return reader(nBytes).decode(ENCODING)

# RESPONSIBLE FOR READING DATA FROM SERVER


class FractalReader:

    def __init__(self):
        super().__init__()

    def read(self, reciever: RecieveFunction):
        payloadlen = StreamHelper.readInt(reciever)
        print(payloadlen)
        payloadNameLen = StreamHelper.readInt(reciever)
        print(payloadNameLen)
        payloadName = StreamHelper.readBytesToString(
            reciever, payloadNameLen)
        print(payloadName)
        segmentsLen = StreamHelper.readInt(reciever)
        segments = StreamHelper.readBytesToString(
            reciever, segmentsLen)
        print(segments)
        jsonLen = StreamHelper.readInt(reciever)
        jsondata = StreamHelper.readBytesToString(
            reciever, jsonLen)
        print(jsondata)
        sj = json.loads(segments)
        for a in sj:
            k = next(iter(a.values()))
            size = k["size"]
            print(size)
            pass


class Segment(metaclass=abc.ABCMeta):

    _META_KEY_SIZE = "size"
    _META_KEY_MIME_TYPE = "mime_type"
    _META_KEY_FILE_NAME = "filename"

    def __init__(self):
        super().__init__()
        self.segmentMeta = {}

    @ abc.abstractmethod
    def writeToStream(self):
        pass

    def getSegmentSize(self):
        return self.segmentMeta.get(self._META_KEY_SIZE)

    def getMeta(self):
        return self.segmentMeta

    def setSegmentSize(self, size: Type[int]):
        self.segmentMeta[self._META_KEY_SIZE] = size

    def setSegmentMimeType(self, mimeType: Type[str]):
        self.segmentMeta[self._META_KEY_MIME_TYPE] = mimeType

    def setSegmentFilename(self, fileName: Type[str]):
        self.segmentMeta[self._META_KEY_FILE_NAME] = fileName


class JsonSegment(Segment):

    def __init__(self, jsonString: str):
        super().__init__()
        self.jsonString = jsonString
        self.setSegmentSize(len(jsonString))
        self.setSegmentMimeType("application/json")

    def writeToStream(self, sendFunction: SendFunction):
        sendFunction(bytes(self.jsonString, ENCODING))


class FileSegment(Segment):

    def __init__(self, file: BufferedReader):
        super().__init__()
        self._file = file
        self.setSegmentSize(os.path.getsize(file.name))
        self.setSegmentMimeType(mimetypes.guess_type(file.name)[0])
        fileName = file.name.split("/")
        self.setSegmentFilename(fileName[len(fileName) - 1])

    def writeToStream(self, sendFunction: SendFunction):
        reader = self._file.read(256)

        while (reader):
            sendFunction(reader)
            reader = self._file.read(256)

        self._file.close()


class Payload:

    def __init__(self, payloadname: str):
        self.payloadname = payloadname
        self.segments = {}
        self.jsonBody = "{}"

    # Adds a segment to the payload
    def addSegment(self, name: str, segment: Segment):
        self.segments[name] = segment

    # Adds json string data payload: string from json.dumps
    def addJsonData(self, jsonString):
        self.jsonBody = jsonString

    # Writes the payload to the stream of the socket
    def writeToStream(self, sendAll: SendFunction):
        # HOLDS TOTAL PAYLOAD SIZE
        totalPayloadSize = 0

        # PAYLOAD NAME
        payloadNameLength = len(self.payloadname)
        payloadNameByteSize = payloadNameLength.to_bytes(
            PAYLOAD_NAME_LENGTH, ENDIANES)
        payloadNameBytes = bytes(self.payloadname, ENCODING)

        totalPayloadSize += payloadNameLength

        # SEGMENTS SIZE AND META
        segments = []
        segmentSize = 0
        for (key, r) in self.segments.items():
            segments.append({key: r.getMeta()})
            segmentSize += r.getSegmentSize()
            totalPayloadSize += r.getSegmentSize()
        # print(f"TOTAL SEGMENT SIZE ", segmentSize)
        # PARSE META TO JSON AND GET LENGTH
        segmentsJson = json.dumps(segments)
        segmentMetaLength = len(segmentsJson)
        segmentMetaByteSize = segmentMetaLength.to_bytes(
            SEGMENTS_LENGTH, ENDIANES)
        segmentMetaBytes = bytes(segmentsJson, ENCODING)

        totalPayloadSize += segmentMetaLength

        # JSON BODY
        jsonBodyLength = len(self.jsonBody)
        jsonBodyBytesSize = jsonBodyLength.to_bytes(JSON_LENGTH, ENDIANES)
        jsonBodyBytes = bytes(self.jsonBody, ENCODING)
        totalPayloadSize += jsonBodyLength

        totalPayloadByteSize = totalPayloadSize.to_bytes(
            PAYLOAD_LENGTH, ENDIANES)

        ## START WRITING ##

        # TOTAL PAYLOAD SIZE
        sendAll(totalPayloadByteSize)

        # PAYLOAD NAME SIZE
        sendAll(payloadNameByteSize)
        # PAYLOAD NAME
        sendAll(payloadNameBytes)

        # SEGMENT META SIZE
        sendAll(segmentMetaByteSize)
        # SEGMENT META
        sendAll(segmentMetaBytes)

        # JSON LENGTH
        sendAll(jsonBodyBytesSize)
        # JSON DATA
        sendAll(jsonBodyBytes)

        # SEGMENTS
        for segment in self.segments.values():
            segment.writeToStream(sendAll)


# q = open("./PythonClient/q/pom.xml", 'rb')
# FileSegment(q)

payload = Payload("authentication")
payload.addJsonData(json.dumps({"identificationId": "RANDOMSTRING HERE"}))

payload.addSegment("JSONSEGMENT", JsonSegment(json.dumps({
    "arraydata": [
        {
            "data": "text",
            "number": 20000
        }
    ], "field": "some field data"})))

payload.addSegment("FILESEGMENT", FileSegment(
    open("./PythonClient/files/pom.xml", "rb")))

payload.writeToStream(client.getSendAllFunction())

FractalReader().read(client.getReciever())
