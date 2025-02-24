let button = document.getElementById("btn-check-outlined");

button.addEventListener("click", () => {
    button.value = button.checked ? "true" : "false";
    console.log(button.value);
})