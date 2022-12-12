
window.redir = where => window.location.replace(where)

window.checkCredentials = (credentials, callback, onError) => fetch(
    '/checkCredentials',
    {method: 'GET', headers: {'Auth-credentials': credentials}}
).then(response => response.text()).then(fullName => {
    if (fullName === 'false') { if (onError != null) onError() }
    else callback(fullName)
})

window.getCredentials = () => getCookie('credentials')

window.parseType = which => { switch (which) {
    case 0: return 'Processor'
    case 1: return 'Motherboard'
    case 2: return 'Graphics processor'
    case 3: return 'Operating memory'
    case 4: return 'Hard drive'
    case 5: return 'Solid state drive'
    case 6: return 'Power supply unit'
    case 7: return 'Cooler'
    case 8: return 'Case'
} }

window.setCookie = (cname, cvalue) => document.cookie = `${cname}=${cvalue};`

function getCookie(cname) {
    const name = cname + "=";
    const decodedCookie = decodeURIComponent(document.cookie);
    const ca = decodedCookie.split(';');

    for(let i = 0; i < ca.length; i++) {
        let c = ca[i];
        while (c.charAt(0) === ' ')
            c = c.substring(1);
        if (c.indexOf(name) === 0)
            return c.substring(name.length, c.length);
    }
    return null;
}
