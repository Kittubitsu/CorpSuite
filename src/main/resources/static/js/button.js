document.addEventListener("DOMContentLoaded", function () {

    let button = document.getElementById("btn-check-outlined");
    let btnLabel = document.getElementsByClassName("btn-outline-primary")[0]

    if (button.checked === true) {
        btnLabel.textContent = "Active"
    } else {
        btnLabel.textContent = "Inactive"
    }

    button.addEventListener("click", () => {
        if (button.checked === true) {
            button.value = "true"
            btnLabel.textContent = "Active"
        } else {
            button.value = "false"
            btnLabel.textContent = "Inactive"
        }
    })
});