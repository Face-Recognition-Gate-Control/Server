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

    payloadSize = 200
    meta = json.dumps({"segments": [
        {
            "size": payloadSize,
            "mime": "application/json"
        }
    ]})

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

    payload = payload = [0] * payloadSize
    payloadByes = bytes(payload, "utf-8")

    s.sendall(payloadNameSize)
    s.sendall(payloadNameByes)
    s.sendall(metaBytesSize)
    s.sendall(metaBytes)
    s.sendall(payloadByes)

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
