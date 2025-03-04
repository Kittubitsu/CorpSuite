document.addEventListener("DOMContentLoaded", () => {
    let firstDay = new Date(Date.now());
    let countEl = document.getElementById("calculated-days");
    let eligibleDaysEl = document.getElementById("eligibleDays");
    let daysErrorEl = document.getElementById("days-error");
    let fromDateEl = document.getElementById("from-date");
    let toDateEl = document.getElementById("to-date");
    let submitButtonEl = document.getElementById("submit-button");

    $('input[id="datepicker"]').daterangepicker({
        minDate: firstDay,
        showWeekNumbers: true,
        autoApply: true,
        locale: {
            format: 'DD/MM/YYYY'
        },
        isInvalidDate: function (date) {
            return (date.day() === 0 || date.day() === 6);
        },

    });

    $('#datepicker').on('apply.daterangepicker', (ev, picker) => {
        daysErrorEl.setAttribute("hidden", "hidden");
        submitButtonEl.removeAttribute("disabled")
        const startDate = new Date(picker.startDate);
        const endDate = new Date(picker.endDate);

        fromDateEl.value = startDate.toISOString().split("T")[0];
        toDateEl.value = endDate.toISOString().split("T")[0];

        let daysBetween = Math.ceil((endDate.getTime() - startDate.getTime()) / (1000 * 60 * 60 * 24));
        let dayCount = 0;
        let initialDate = startDate;

        for (let i = 0; i < daysBetween; i++) {
            let actualDayNum = new Date(initialDate).getDay();
            if ([6, 0].includes(actualDayNum)) {
                initialDate.setDate(initialDate.getDate() + 1);
                continue;
            }
            dayCount = dayCount + 1;
            initialDate.setDate(initialDate.getDate() + 1);
        }
        if (eligibleDaysEl.value < dayCount || dayCount === 0) {
            daysErrorEl.removeAttribute("hidden");
            submitButtonEl.setAttribute("disabled", "disabled")
        }
        countEl.value = dayCount;
    })
})


