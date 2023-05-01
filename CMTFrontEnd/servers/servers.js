const tcpSocket = require('net').Socket()
const httpServer = require('http').createServer()
const Server = require('socket.io').Server
const cors = require('cors')
const upload = require('express-fileupload')
const express = require('express')


const app = express()
app.use(upload())
app.use(cors({origin: "*"}))
app.listen(8081, () => {console.log("listening on port 8081")})

var userData
var allUsers = []
var confData
var userIdConf

app.get('/home', (req, res) => {
    res.render("/home/muano/Desktop/ConferenceManagementTool/CMTFrontEnd/views/ejs/home.ejs", {
        userData: userData,
        allUsers: allUsers,
    })
})
app.get('/login', (req, res) => {
    res.render("/home/muano/Desktop/ConferenceManagementTool/CMTFrontEnd/views/ejs/login.ejs")
})
app.get('/signup', (req, res) => {
    res.render("/home/muano/Desktop/ConferenceManagementTool/CMTFrontEnd/views/ejs/signup.ejs")
})
app.get('/conference', (req, res) => {
    res.render("/home/muano/Desktop/ConferenceManagementTool/CMTFrontEnd/views/ejs/conference.ejs", {
        confData: confData,
        userIdConf : userIdConf
    })
})
app.get('/fileinput', (req, res) => {
    res.render("/home/muano/Desktop/ConferenceManagementTool/CMTFrontEnd/views/ejs/fileinput.ejs")
})
app.get('/models/app.js', (req, res) => {
    res.sendFile("/home/muano/Desktop/ConferenceManagementTool/CMTFrontEnd/views/models/app.js")
})
app.get('/models/gateway.js', (req, res) => {
    res.sendFile("/home/muano/Desktop/ConferenceManagementTool/CMTFrontEnd/views/models/gateway.js")
})










/*
    SOCKET.IO SOCKET CONNECTION WHICH COMMUNICATES VIA THE TCP SOCKET CONNECTED
    TO THE BACKEND
*/
// const httpServer = createServer
const io = new Server(8080, {cors: {origin: "*"}})

io.on('connection', (socket) => {
    console.log("------ gateway.js and servers.js connected ------")
    var batchData = ""
    var fileSize = 0
    var counter = -1

    /*
    TCP SOCKET CONNECTION WHICH IS A MEDIATOR BETWEEN THE SOCKET.IO SOCKET
    AND THE BACK END GATEWAY TCP CONNECTION
    */

    tcpSocket.connect(13000, "127.0.0.1", () => {
        console.log("TCP connection to 127.0.0.1:13000 established")
    })

    tcpSocket.on("close", (reason) => {
        console.log("TCP connection to 127.0.0.1:13000 closed because: " + reason)
    })

    tcpSocket.on('data', (tcpSocketRes) => {

        if (tcpSocketRes.toString() == "send signup details")
        {
            console.log("\n response from tcpSocket : " + tcpSocketRes.toString())
            socket.emit("send signup details", tcpSocketRes.toString())
        }
        else if (tcpSocketRes.toString() == "send pword")
        {
            console.log("\n response from tcpSocket (send pword) : " + tcpSocketRes.toString())
            socket.emit("send pword", tcpSocketRes.toString())
            console.log("emitted 'send pword' to client" + tcpSocketRes.toString())
        }
        else if (tcpSocketRes.toString() == "signup successful")
        {
            console.log("\n response from tcpSocket : " + tcpSocketRes.toString())
            socket.emit("signup successful", tcpSocketRes.toString())
        }
        else if (tcpSocketRes.toString() == "incorrect pword or email")
        {
            console.log("response from tcpSocket : " + tcpSocketRes.toString())
            socket.emit("incorrect pword or email", tcpSocketRes.toString())
        }
        else if (tcpSocketRes.toString().includes(">>"))
        {
            console.log("response from tcpSocket : " + tcpSocketRes.toString())
            // get all userData belonging to the user
            userData = JSON.parse(tcpSocketRes.toString().split(">>")[1])
            socket.emit('logged in', tcpSocketRes.toString())
        }
        else if (tcpSocketRes.toString().includes("**"))
        {
            console.log("response from tcpSocket : " + tcpSocketRes.toString())
            // get all confData belonging to the conference
            confData = JSON.parse(tcpSocketRes.toString().split("**")[1])
            // confData = "This is pretty much working"
            socket.emit('confData received', tcpSocketRes.toString())
        }
        else if (tcpSocketRes.toString().includes("<<"))
        {
            var responseStr = tcpSocketRes.toString().split("<<")[1]
            console.log("response from tcpSocket : " + responseStr)
            allUsers = JSON.parse(responseStr)
            // socket.emit('allusers', responseStr)
        }
        else if (tcpSocketRes.toString() == "send submission abstract")
        {
            socket.emit("send submission abstract", tcpSocketRes.toString())
        }
        else if (tcpSocketRes.toString() == "conference created")
        {
            console.log("response from tcpSocket : " + tcpSocketRes.toString())
            socket.emit("conference created", tcpSocketRes.toString())
        }
        else if (tcpSocketRes.toString() == "send file to be uploaded")
        {
            socket.emit("send file to be uploaded", tcpSocketRes.toString())
            console.log("step5: request for file to be uploaded received from Gateway.java, and emitted to gateway.js")
        }
        else if (tcpSocketRes.toString() == "what file?")
        {
            socket.emit("what file?", tcpSocketRes.toString())
            console.log("step5: request for file name received from Gateway.java, and emitted to gateway.js")
        }
        else if (tcpSocketRes.toString().includes("^^"))
        {
            if (tcpSocketRes.toString().split("^^")[1].length == 8000)
            {
                counter = counter + 1
                console.log("the length of the batch in step14 is " + tcpSocketRes.toString().split("^^")[1].length)

                // console.log(tcpSocketRes.toString().split("^^")[1])
                batchData = batchData + tcpSocketRes.toString().split("^^")[1]
                tcpSocket.write("ready to receive next batch\n")
                console.log("\nstep14: ready to receive next batch")
                console.log("step14 counter is " + counter)
            }
            else if (tcpSocketRes.toString().split("^^")[2] == "fileSize")
            {
                console.log("the tcpResponse from step10 in Gateway.js is " + tcpSocketRes.toString())

                fileSize = parseInt(tcpSocketRes.toString().split("^^")[1])
                tcpSocket.write(fileSize.toString() + "\r\n")
                console.log("step11: fileSize received is " + fileSize + ", and an acknowledgement has been sent to Gateway.java")
            }
            else
            {
                counter = counter + 1
                console.log("the length of the batch in step15 " + tcpSocketRes.toString().split("^^")[1].length)

                batchData = batchData + tcpSocketRes.toString().split("^^")[1]
                tcpSocket.write("thank you\r\n")

                socket.emit("your file", batchData + "@" + fileSize)
                console.log("step16: the whole batch has been received and sent to the gateway.js")
                console.log("the counter when the last batch is sent is " + counter)
            }
        }
        else if (tcpSocketRes.toString() == "upload is successful")
        {
            socket.emit("upload is successful", tcpSocketRes.toString())
            console.log("step11: emitted to client that upload is successful")
        }
        else if (tcpSocketRes.toString() == "for which and date")
        {
            socket.emit("for which and date", tcpSocketRes.toString())
        }
    })

    socket.on('request message', (reqMsg) => {
        console.log("\nmessege from gateway ---> " + reqMsg.toString())
        tcpSocket.write(reqMsg.toString() + "\r\n")
        console.log("step2: request message received from gateway, now sending to Gateway.java")
    })

    socket.on("userIdConf", (receivedUserIdConf) => {
        userIdConf = receivedUserIdConf
    })

    socket.on("submission abstract", (submissionAbstract) => {
        console.log(submissionAbstract)
        tcpSocket.write(submissionAbstract.toString() + "\r\n")
    })

    socket.on('signup details', (signudetails) => {
        tcpSocket.write(signudetails.toString() + "\r\n")
    })

    socket.on('client pword', (pword) => {
        tcpSocket.write(pword.toString() + "\r\n")
    })

    socket.on('forWhich@Date', (data) => {
        tcpSocket.write(data + "\r\n")
    })

    socket.on('my file', (Uint8ArrayString) => {
        tcpSocket.write(Uint8ArrayString + "\r\n")
        console.log("step7: file of length " + Uint8ArrayString.length + " emitted by the client has been sent to Gateway.java")

    })

    socket.on('file name', (fileName) => {
        tcpSocket.write(fileName + "\r\n")
        console.log("step7: received file name is " + fileName + " and has be sent to the Gateway.java")
    })

    socket.on("batchData received", () => {
        batchData = ""
        fileSize = 0
        counter = -1
        console.log("step20: all is done!")
    })

    socket.on('disconnect', (reason) => {
        console.log("------ gateway.js and servers.js are disconnected ------")
    })
})
// httpServer.listen(8080, () => console.log('listening on http://localhost:8080'))