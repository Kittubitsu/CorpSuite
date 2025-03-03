document.addEventListener("DOMContentLoaded", () => {
    let firstDay = new Date(Date.now());
    $('input[id="datepicker"]').daterangepicker({
        minDate: firstDay,
        autoApply: true,
        locale: {
            format: 'DD/MM/YYYY'
        },
        isInvalidDate: function (date) {
            return (date.day() === 0 || date.day() === 6);
        },

    });

    $('#datepicker').on('apply.daterangepicker', (ev, picker) => {

        let countEl = document.getElementById("calculated-days");
        let fromDate = document.getElementById("fromDate");
        let toDate = document.getElementById("toDate");

        const startDate = new Date(picker.startDate);
        const endDate = new Date(picker.endDate);
        fromDate.value = startDate.toISOString();
        toDate.value = endDate.toISOString();
        let daysBetween = Math.round((endDate.getTime() - startDate.getTime()) / (1000 * 60 * 60 * 24));
        let dayCount = 0;
        let initialDate = startDate;

        for (let i = 0; i < daysBetween; i++) {
            let actualDayNum = new Date(initialDate).getUTCDay();
            if ([6, 0].includes(actualDayNum)) {
                initialDate.setDate(initialDate.getDate() + 1);
                continue;
            }
            dayCount = dayCount + 1;
            initialDate.setDate(initialDate.getDate() + 1);
        }



        countEl.value = dayCount;


    })
})


