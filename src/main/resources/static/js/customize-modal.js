document.addEventListener("DOMContentLoaded", () => {

    let errorFields = document.getElementsByClassName("error-field");
    const myModal = new bootstrap.Modal('#add-user');


    if (errorFields.length > 0) {
        myModal.show();
    }
})