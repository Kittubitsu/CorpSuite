document.addEventListener("DOMContentLoaded", function () {

    let tableBodyElement = document.querySelector("table tbody");
    const months = ["Януари", "Февруари", "Март", "Април", "Май", "Юни", "Юли", "Август", "Септември", "Октомври", "Ноември", "Декември"];

    let clearBtnEl = document.getElementById("clear-log-btn");

    let rowsPerPage = getRowsPerPage();


    function getTableAvailableHeight() {
        let tableElement = document.querySelector(".log-overview")
        let tableElementArr = [...tableElement.children]

        let mainElHeight = tableElement.clientHeight - 40; //accounting for the padding

        tableElementArr.forEach(element => {
            if (element.clientHeight < 100) {
                mainElHeight = mainElHeight - element.clientHeight;
            }
        })

        return mainElHeight - ((45 + 24) + 14)
    }

    function getCellHeight() {
        let tableElement = document.querySelector(".log-overview table tbody tr:last-of-type")

        if (tableElement === null || tableElement.clientHeight === 0) {
            return 66
        }

        return tableElement.clientHeight;
    }

    function getRowsPerPage() {
        let availableTableHeight = getTableAvailableHeight();
        let cellHeight = getCellHeight();

        return Math.floor(availableTableHeight / cellHeight);
    }


    clearBtnEl.addEventListener("click", () => {
        setTimeout(() => {
            location.reload();
        }, 100)
    })

    fetch("api/v1/logs").then(res => {
        if (!res.ok) {
            throw new Error(res.status.toString());
        }
        return res.json();
    }).then(result => {
        Object.values(result).forEach(record => {
            const {email, action, module, comment, timestamp} = record

            let time = new Date(timestamp.toString());
            let formattedTime = `${time.getDate().toString().padStart(2, '0')}-${months[time.getMonth()]}-${time.getFullYear()} ${time.getHours().toString().padStart(2, '0')}:${time.getMinutes().toString().padStart(2, '0')}:${time.getSeconds().toString().padStart(2, '0')}`

            let rowElement = document.createElement("tr");

            rowElement.style.display = "none"

            let cellEmailElement = document.createElement("td");
            cellEmailElement.textContent = email.toString();

            let cellActionElement = document.createElement("td");
            cellActionElement.textContent = action.toString();

            let cellModuleElement = document.createElement("td");
            cellModuleElement.textContent = module.toString();

            let cellCommentElement = document.createElement("td");
            cellCommentElement.textContent = comment.toString();

            let cellTimestampElement = document.createElement("td");
            cellTimestampElement.textContent = formattedTime;

            rowElement.appendChild(cellEmailElement);
            rowElement.appendChild(cellActionElement);
            rowElement.appendChild(cellModuleElement);
            rowElement.appendChild(cellCommentElement);
            rowElement.appendChild(cellTimestampElement);

            tableBodyElement.appendChild(rowElement)
        });

        setupPagination();
    });

    function setupPagination() {
        let tableRows = tableBodyElement.children;
        let tableElements = [...tableRows];

        let pageList = [];
        let count = 1;
        let rowsPerPage = getRowsPerPage();

        for (let i = 0; i < tableRows.length; i += rowsPerPage) {
            pageList.push(count++);
        }

        let navDivElement = document.createElement("div");

        navDivElement.classList.add("table-nav-bottom");

        pageList.forEach(pageNum => {
            let anchorElement = document.createElement("button");
            anchorElement.classList.add("table-nav-bottom-button")
            anchorElement.textContent = pageNum;
            if (pageNum === 1) anchorElement.setAttribute("disabled", "disabled");
            anchorElement.addEventListener("click", changePage);
            navDivElement.appendChild(anchorElement);
        })

        let parentDivElement = tableBodyElement.parentElement.parentElement;
        parentDivElement.appendChild(navDivElement);

        function changePage(e) {
            let currentTarget = e.currentTarget;
            let selectedPage = Number.parseInt(currentTarget.textContent)

            let buttonList = document.getElementsByClassName("table-nav-bottom-button");
            for (let buttonListElement of buttonList) {
                buttonListElement.removeAttribute("disabled");
            }
            currentTarget.setAttribute("disabled", "disabled");

            tableElements.forEach(row => row.style.display = "none");

            let startId = (selectedPage - 1) * rowsPerPage;
            let endId = startId + rowsPerPage;
            tableElements.slice(startId, endId).forEach(row => row.style.display = "table-row");
        }

        tableElements.forEach(row => row.style.display = "none");
        tableElements.slice(0, rowsPerPage).forEach(rowsPerPage => rowsPerPage.style.display = "table-row");
    }

    window.addEventListener("resize", () => {
        rowsPerPage = getRowsPerPage();
        document.querySelector(".table-nav-bottom")?.remove();  // Remove existing pagination
        setupPagination(); // Recreate pagination based on new rowsPerPage
    });

    // scrapping this filtering/sorting idea for now, will probably come back to it eventually

    // let emails = new Set;
    // tableElements.forEach(row => emails.add(row.children[0].innerHTML));
    //
    // let selectEl = document.getElementById("email-select");
    //
    // emails.forEach(user => {
    //     let optionElement = document.createElement("option");
    //     optionElement.value = user;
    //     optionElement.textContent = user;
    //     selectEl.appendChild(optionElement);
    // });

    // selectEl.addEventListener("change", (e) => {
    //     let key = e.currentTarget.value;
    //     if (key === '') {
    //         return;
    //     }
    //     tableElements.forEach(row => {
    //         let data = row.children[0].innerHTML;
    //         if (data.includes(key)) {
    //             row.style.display = "table-row"
    //         } else {
    //             row.style.display = "none"
    //         }
    //     })
    // })

    // console.log(emails);
});