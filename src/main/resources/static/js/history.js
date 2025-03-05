document.addEventListener("DOMContentLoaded", function () {

    let historyBtnEl = document.getElementById("history-button");

    historyBtnEl.addEventListener('click', (e) => {
        let buttonLinkArr = historyBtnEl.href.split('=');
        let buttonBool = buttonLinkArr[1];
        if (buttonBool === 'true') {
            historyBtnEl.href = buttonLinkArr[0].concat('=false')
        } else {
            historyBtnEl.href = buttonLinkArr[0].concat('=true')
        }
    })
})