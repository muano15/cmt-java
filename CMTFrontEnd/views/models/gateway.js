// String formart => userid/useremail::action::confid/confname::role::attributes@confOrg=org1,org2/null/null

export function MakeCoference(confName, confOrganisers, confAreachairs, confReviewers, confAuthors, confMode) {
    const clientSocket = io('ws://localhost:8080')

    var confOrg = ""
    for (var i = 0; i < confOrganisers.length; i++) {
        if (i != confOrganisers.length - 1) {
            confOrg = confOrg + confOrganisers[i] + ","
        } else {
            confOrg = confOrg + confOrganisers[i]
        }
    }

    var confAch = ""
    for (var i = 0; i < confAreachairs.length; i++) {
        if (i != confAreachairs.length - 1) {
            confAch = confAch + confAreachairs[i] + ","
        } else {
            confAch = confAch + confAreachairs[i]
        }
    }

    var confRev = ""
    for (var i = 0; i < confReviewers.length; i++) {
        if (i != confReviewers.length - 1) {
            confRev = confRev + confReviewers[i] + ","
        } else {
            confRev = confRev + confReviewers[i]
        }
    }

    var confAth = ""
    for (var i = 0; i < confAuthors.length; i++) {
        if (i != confAuthors.length - 1) {
            confAth = confAth + confAuthors[i] + ","
        } else {
            confAth = confAth + confAuthors[i]
        }
    }
    console.log("47::MakeConference::" + confName.toString() + "::admin::attributes@confOrg=" + confOrg + "/confAch=" + confAch + "/confRev=" + confRev + "/confAth=" + confAth + "/confMode=" + confMode.toString())
    clientSocket.emit("request message", "47::MakeConference::" + confName.toString() + "::admin::attributes@confOrg=" + confOrg + "/confAch=" + confAch + "/confRev=" + confRev + "/confAth=" + confAth + "/confMode=" + confMode.toString() + "\r\n")

    clientSocket.on("conference created")
    {
        document.querySelector("#homeCreateConfModalBody").innerHTML = "Conference created successfully"
    }
}
export function DeleteConference(userId, confId) {
    var clientSocket = CreateSocketConnection()

    console.log(userId.toString() + "::DeleteConference::" + confId.toString() + "::admin::null")
    clientSocket.write(userId.toString() + "::DeleteConference::" + confId.toString() + "::admin::null\r\n")
}
export function AddOrganiser(userId, confId, confOrganiser) {
    var clientSocket = CreateSocketConnection()

    var confOrg = ""
    for (var i = 0; i < confOrganiser.length; i++) {
        if (i != confOrganiser.length - 1) {
            confOrg = confOrg + confOrganiser[i] + ","
        } else {
            confOrg = confOrg + confOrganiser[i]
        }
    }
    console.log(userId.toString() + "::AddOrganiser::" + confId.toString() + "::admin::attributes@confOrg=" + confOrg + "/null")
    clientSocket.write(userId.toString() + "::AddOrganiser::" + confId.toString() + "::admin::attributes@confOrg=" + confOrg + "/null\r\n")
}
export function RemoveOrganiser(userId, confName, confOrganiser) {
    var clientSocket = CreateSocketConnection()

    var confOrg = ""
    for (var i = 0; i < confOrganiser.length; i++) {
        if (i != confOrganiser.length - 1) {
            confOrg = confOrg + confOrganiser[i] + ","
        } else {
            confOrg = confOrg + confOrganiser[i]
        }
    }
    console.log(userId.toString() + "::RemoveOrganiser::" + confName.toString() + "::admin::attributes@confOrg=" + confOrg + "/null")
    clientSocket.write(userId.toString() + "::RemoveOrganiser::" + confName.toString() + "::admin::attributes@confOrg=" + confOrg + "/null\r\n")
}
export function Login(email, pword) {
    const clientSocket = io('ws://localhost:8080')

    clientSocket.emit("request message", email + "::Login::null::null::null")

    clientSocket.on("send pword", (data) => {
        clientSocket.emit('client pword', pword)
        console.log("pword emitted: " + pword)
    })

    clientSocket.on("incorrect pword or email", (JSONUserData) => {
        clientSocket.disconnect()
    })

    clientSocket.on("logged in", (JSONUserData) => {
        clientSocket.disconnect()
        window.open('http://localhost:8081/home', '_top')
    })
}
export function Signup(name, email, pword, expertise, domian) {
    const clientSocket = io('ws://localhost:8080')

    clientSocket.emit("request message", "null::Signup::null::null::null")

    clientSocket.on("send signup details", () => {
        // Signup details string => name :: email :: pword :: expertise1,expertise2 :: domain1,domain2
        clientSocket.emit("signup details", name + "::" + email + "::" + pword + "::" + expertise +  "::" +  domian)
        console.log("signup details sent")
    })

    clientSocket.on('signup successful', () => {
        clientSocket.disconnect()
        const template = document.createElement("template")
        template.innerHTML = "<p>You have successfully signedup</p>".trim()
        document.body.appendChild(template.content.firstElementChild)

        window.open('http://localhost:8081/home', '_top')
    })
}
export function GetAllUsers() {
    const clientSocket = io('ws://localhost:8080')

    clientSocket.emit("request message", "null::GetAllUsers::null::null::null")

    clientSocket.on('allusers', data => {
        allUsers = JSON.parse(data.toString().split("<<")[1])
    })
}
export function GetUserData(userId)
{
    const clientSocket = io('ws://localhost:8080')

    clientSocket.emit("request message", userId.toString() +"::GetUserData::null::null::null")
}
export var allUsers = "text"

