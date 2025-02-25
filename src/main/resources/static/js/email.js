document.addEventListener("DOMContentLoaded", function () {

    let emailField = document.getElementById("email");
    let firstNameField = document.getElementById("first-name");
    let lastNameField = document.getElementById("last-name");

    firstNameField.addEventListener("change", (e) => {
        emailField.value = firstNameField.value.toString().toLowerCase();
    })

    lastNameField.addEventListener("change", () => {
        emailField.value += "." + lastNameField.value.toString().toLowerCase() + "@corpsuite.com";
    })

});