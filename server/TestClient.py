import socket
import time
import os
import json

HOST = 'localhost'    # The remote host
PORT = 9876         # The same port as used by the server

PAYLOAD_NAME_LENGTH = 2
META_LENGTH = 4

with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as s:
    s.connect((HOST, PORT))

    payloadName = "authentication"

    jsonData = json.dumps({"arraydata": [
        {
            "data": "text",
            "number": 20000
        }
    ],
        "field": "some field data"
    })

    textData = "I AM SOME TEXT HERE AND I WANT TO BE IN A FILE"
    meta = json.dumps({"segments": [
        {
            "size": len(jsonData),
            "mime_type": "application/json",
            "filename": "ajsonfile.json",
        },
        {
            "size": len(textData),
            "mime_type": "application/txt",
            "filename": "textfile.txt",
        }
    ], "username": "freshfish"})

    payloadNameSize = len(payloadName).to_bytes(PAYLOAD_NAME_LENGTH, 'big')
    payloadNameByes = bytes(payloadName, "utf-8")

    metaSize = len(meta)
    #
    # metaSizeFile = os.path.getsize("./windows.iso")

    # print(metaSizeFile)
    # print(bin(metaSizeFile))
    # print(metaSizeFile.to_bytes(length=8, byteorder='big'))

    metaBytesSize = metaSize.to_bytes(META_LENGTH, 'big')
    metaBytes = bytes(meta, "utf-8")

    payloadBytes1 = bytes(jsonData, "utf-8")
    payloadBytes2 = bytes(textData, "utf-8")
    s.sendall(payloadNameSize)
    s.sendall(payloadNameByes)
    s.sendall(metaBytesSize)
    s.sendall(metaBytes)
    s.sendall(payloadBytes1)
    s.sendall(payloadBytes2)
    # s.sendall(payloadBytes2)

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
