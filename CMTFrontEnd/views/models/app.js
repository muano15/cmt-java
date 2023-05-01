import {
    Signup,
    Login,
    GetAllUsers,
    GetUserData,
    MakeCoference,
    DeleteConference,
    RemoveMember,
    AddMember,
    UploadFile,
    DownloadFile,
    CreateSubmission,
    SetDueDate
} from './gateway.js'
// import {Buffer} from "node:buffer";

const refreshBtn = document.querySelector("#refreshBtn")
if (refreshBtn){
    refreshBtn.addEventListener("click", () => {
        console.log(refreshBtn.value)
        console.log("at least something worked")
        GetUserData(refreshBtn.value)
        window.setTimeout(() =>{ window.location.reload() }, 500)
    })
}

const loginBtn = document.querySelector("#loginBtn")
if (loginBtn) {
    loginBtn.addEventListener("click", () =>{
        Login(
            document.querySelector("#loginEmail").value,
            document.querySelector("#loginPword").value
        )
    })
}

const signupSubmitBtn = document.querySelector("#signupSubmitBtn")
if (signupSubmitBtn) {
    signupSubmitBtn.addEventListener("click", () => {
        if (document.querySelector("#signupUserPword").value == document.querySelector("#signupVerifyUserPword").value) {
            Signup(
                document.querySelector("#signupUserName").value,
                document.querySelector("#signupUserEmail").value,
                document.querySelector("#signupUserPword").value,
                document.querySelector("#signupUserExpertise").value,
                document.querySelector("#signupDomain").value
            )
        } else {
            console.log("Your passwords do not match")
        }
    })
}

const homeCreateConfBtn = document.querySelector("#homeCreateConfBtn")
if (homeCreateConfBtn)
{
    homeCreateConfBtn.addEventListener("click", () => {
        console.log("I am clicked")
        console.log(sessionStorage.getItem('organisers'))

        let confName = document.querySelector("#newConferenceName").value
        let confOrganisers = JSON.parse(sessionStorage.getItem('organisers'))
        let confAreachairs = JSON.parse(sessionStorage.getItem('areachairs'))
        let confReviewers = JSON.parse(sessionStorage.getItem('reviewers'))
        let confAuthors = JSON.parse(sessionStorage.getItem('authors'))
        let modes = document.getElementsByName('newConferenceType')

        let confMode
        modes.forEach(radio => {
            if (radio.checked)
            {
                if (radio.value.toString() == "single-blind")
                {
                    confMode = "0"
                }
                else
                {
                    confMode = "1"
                }
            }
        })

        console.log("Making conference and confMode is " + confMode)
        MakeCoference(confName, confOrganisers, confAreachairs, confReviewers, confAuthors, confMode)
    })
}

// const memberSearchBar = document.querySelector("#memberSearchBar")
// memberSearchBar.addEventListener("input", e => {
//     const value = e.target.value
//     console.log(value)
// })

const addMembersBtn = document.querySelector("#addMembersBtn")
if (addMembersBtn)
{
    addMembersBtn.addEventListener("click", () => {
        if (sessionStorage.getItem('organisers') == null)
        {
            console.log("in the sessionStorage loop")
            sessionStorage.setItem("organisers", JSON.stringify([]))
            sessionStorage.setItem("areachairs", JSON.stringify([]))
            sessionStorage.setItem("reviewers", JSON.stringify([]))
            sessionStorage.setItem("authors", JSON.stringify([]))
        }
    })
}

const newConfBtn = document.querySelector("#newConfBtn")
if (newConfBtn)
{
    newConfBtn.addEventListener("click", () => {
        GetAllUsers()
    })
}

const uploadFileBtn = document.querySelector("#uploadFileBtn")
if (uploadFileBtn)
{
    uploadFileBtn.addEventListener("click", (e) => {
        e.preventDefault()

        let file = document.querySelector("#formFile").files[0]

        const blob = new Blob([file], {type: file.type})

        blob.arrayBuffer().then((arrayBuffer) => {
            var myUint8Array = new Uint8Array(arrayBuffer)

            var Uint8ArrayString = myUint8Array.toString()

            UploadFile(file.type, Uint8ArrayString)
        })
    })
}

const downloadFileBtn = document.querySelector("#downloadFileBtn")
if (downloadFileBtn)
{
    downloadFileBtn.addEventListener("click", (e) => {
        e.preventDefault()

        DownloadFile()
    })
}

const createSubmissionBtn = document.querySelector("#createSubmissionBtn")
if (createSubmissionBtn)
{
    createSubmissionBtn.addEventListener("click", () => {
        var submissionAbstract = document.querySelector("#addSubmissionAbstractTextArea").value.toString()
        var confId = myConfIdVar
        var userId = myUserIdVar

        console.log(userId)
        console.log(submissionAbstract)

        CreateSubmission(confId, userId, submissionAbstract)
    })
}

const setConfAbstDday = document.querySelector("#setConfAbstDday")
if (setConfAbstDday)
{
    setConfAbstDday.addEventListener("click", () => {
        const dateInput = document.createElement("div")
        dateInput.innerHTML =
            '<div class="m-3 text-center">' +
                '<div class="row my-3">' +
                    '<input id="abstDateInput" class="form-control" type="text" placeholder="YYYY-MM-DD" aria-label="default input example">' +
                '</div>' +
                '<div class="row my-3 justify-content-center">' +
                    '<i id="approveAbstDate" class="col-5 bi bi-check-square h2"></i>' +
                    '<i id="cancelAbstDate" class="col-5 bi bi-x-square h2"></i>' +
                '</div>' +
            '</div>'

        document.getElementById("viewConfAbstDday").remove()
        document.getElementById("setConfAbstDday").remove()
        document.getElementById("forAbstract").appendChild(dateInput)

        const cancelAbstDate = document.querySelector("#cancelAbstDate")
        {
            if (cancelAbstDate)
            {
                cancelAbstDate.addEventListener("click", () => {
                    window.location.reload()
                })
            }
        }

        const approveAbstDate = document.querySelector("#approveAbstDate")
        {
            if (approveAbstDate)
            {
                approveAbstDate.addEventListener("click", () => {
                    SetDueDate(myConfIdVar, "abstract", document.querySelector("#abstDateInput").value.toString())
                })
            }
        }
    })
}

const setConfPaperDday = document.querySelector("#setConfPaperDday")
if (setConfPaperDday)
{
    setConfPaperDday.addEventListener("click", () => {
        const dateInput = document.createElement("div")
        dateInput.innerHTML =
            '<div class="m-3 text-center">' +
                '<div class="row my-3">' +
                    '<input id="paperDateInput" class="form-control" type="text" placeholder="YYYY-MM-DD" aria-label="default input example">' +
                '</div>' +
                '<div class="row my-3 justify-content-center">' +
                    '<i id="approvePaperDate" class="col-5 bi bi-check-square h2"></i>' +
                    '<i id="cancelPaperDate" class="col-5 bi bi-x-square h2"></i>' +
                '</div>' +
            '</div>'

        document.getElementById("viewConfPaperDday").remove()
        document.getElementById("setConfPaperDday").remove()
        document.getElementById("forPaper").appendChild(dateInput)

        const cancelPaperDate = document.querySelector("#cancelPaperDate")
        {
            if (cancelPaperDate)
            {
                cancelPaperDate.addEventListener("click", () => {
                    window.location.reload()
                })
            }
        }

        const approvePaperDate = document.querySelector("#approvePaperDate")
        {
            if (approvePaperDate)
            {
                approvePaperDate.addEventListener("click", () => {
                    SetDueDate(myConfIdVar, "paper", document.querySelector("#paperDateInput").value.toString())
                })
            }
        }
    })
}










