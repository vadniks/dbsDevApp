
window.redir = where => window.location.replace(where)

window.checkCredentials = (credentials, callback) => fetch(
    '/checkCredentials',
    {method: 'GET', headers: {'Auth-credentials': credentials}}
).then(response => response.text()).then(text => {
    if (text === 'null') alert('Wrong credentials')
    else callback()
})

window.credentials = () => {
    const eqIndex = document.cookie.indexOf('=')
    if (eqIndex === -1) return null
    return document.cookie.substring(eqIndex + 1)
}
