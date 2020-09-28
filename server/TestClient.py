import socket
import time
import os
import json

HOST = 'localhost'    # The remote host
PORT = 9876         # The same port as used by the server

# TOTAL PAYLOAD SIZE
PAYLOAD_LENGTH = 4
# SIZE OF THE PAYLOAD IDENTIFIER
PAYLOAD_NAME_LENGTH = 2
# SIZE OF SEGMENTS HEADER
SEGMENTS_LENGTH = 4
# SIZE OF JSON PAYLOAD
JSON_LENGTH = 4

with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as s:
    s.connect((HOST, PORT))

    # SEGMENT 1
    jsonData = json.dumps({"arraydata": [
        {
            "data": "text",
            "number": 20000
        }
    ],
        "field": "some field data"
    })
    jsonDataLength = len(jsonData)

    # SEGMENT 2
    textData = "I AM SOME TEXT HERE AND I WANT TO BE IN A FILE"
    textDataLength = len(textData)

    # PAYLOAD IDENTIFIER
    payloadName = "authentication"
    payloadNameLength = len(payloadName)
    payloadNameSize = payloadNameLength.to_bytes(PAYLOAD_NAME_LENGTH, 'big')
    payloadNameByes = bytes(payloadName, "utf-8")

    # JSON PAYLOAD
    jsonPayload = json.dumps(
        {
            "identificationId": "werlkjwelr5t468r7ter87"
        }
    )
    jsonByteLength = len(jsonPayload)
    jsonByteSize = jsonByteLength.to_bytes(JSON_LENGTH, 'big')
    jsonBytes = bytes(jsonPayload, "utf-8")

    payloadBytes1 = bytes(jsonData, "utf-8")
    payloadBytes2 = bytes(textData, "utf-8")

    # SEGMENT HEADER
    segments = json.dumps([
        {
            "vectorfile":
            {
                "size": jsonDataLength,
                "mime_type": "application/json",
                "random_key": "value of random key"
            }
        },
        {
            "logfile":
            {
                "size": textDataLength,
                "mime_type": "application/txt",
                "filename": "textfile.txt",
            }
        }]
    )
    segmentsBytesLength = len(segments)
    segmentsBytesSize = segmentsBytesLength.to_bytes(SEGMENTS_LENGTH, 'big')
    segmentsBytes = bytes(segments, "utf-8")

    totalBytes = payloadNameLength + segmentsBytesLength + \
        jsonByteLength + jsonDataLength + textDataLength

    s.sendall(totalBytes.to_bytes(PAYLOAD_LENGTH, 'big'))
    print(totalBytes)
    # Payload name
    s.sendall(payloadNameSize)
    s.sendall(payloadNameByes)

    # segments
    s.sendall(segmentsBytesSize)
    s.sendall(segmentsBytes)

    # Json data
    s.sendall(jsonByteSize)
    s.sendall(jsonBytes)

    # Segments data
    s.sendall(payloadBytes1)
    s.sendall(payloadBytes2)

    # SEND FILE:

    # filename = './windows.iso'
    # fileSize = os.path.getsize(filename)

    # f = open(filename, 'rb')
    # l = f.read(1024)

    # f.close()
    # while (l):
    #     s.send(l)
    #     l = f.read(1024)
    # f.close()

    data = s.recv(1024)
print('Received', repr(data))
