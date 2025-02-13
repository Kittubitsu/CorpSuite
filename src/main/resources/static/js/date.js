const pickerFrom = document.getElementById('from');
pickerFrom.addEventListener('input', function (e) {
    var day = new Date(this.value).getUTCDay();
    if ([6, 0].includes(day)) {
        e.preventDefault();
        this.value = '';
        alert('You can not select Weekends!');
    }
});

const pickerTo = document.getElementById('to');
pickerTo.addEventListener('input', function (e) {
    var day = new Date(this.value).getUTCDay();
    if ([6, 0].includes(day)) {
        e.preventDefault();
        this.value = '';
        alert('You can not select Weekends!');
    }
});