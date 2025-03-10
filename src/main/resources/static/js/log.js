document.addEventListener("DOMContentLoaded", function () {

    let tableBodyElement = document.querySelector("table tbody");
    let tableRows = tableBodyElement.children;
    let tableElements = Array.from(tableRows);
    let pageList = [];
    let count = 1;
    let rowsPerPage = 9;

    for (let i = 0; i < tableRows.length; i += 9) {
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
})