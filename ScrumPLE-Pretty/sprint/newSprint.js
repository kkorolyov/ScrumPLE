const sprintForm = document.getElementById("createSprint")

sprintForm.addEventListener("submit", event => {
    event.preventDefault();
    var sprintNumber = sprintForm.sprintNumber.value
    var smonth = sprintForm.smonth.value
    var sday = sprintForm.sday.value
    var syear = sprintForm.syear.value
    var emonth = sprintForm.emonth.value
    var eday = sprintForm.eday.value
    var eyear = sprintForm.eyear.value
    var _str = '{"sprintNumber":"'+Number(sprintNumber)+'","syear":"'+Number(syear)+'","smonth":'+Number(smonth)+'","sdayOfMonth":"'+Number(sday)+'","year":"'+Number(eyear)+'","month":"'+Number(emonth)+'","dayOfMonth":"'+Number(eday)+'}';

    rest.ajax('POST', url, _str, response => displayRaw(response));
})