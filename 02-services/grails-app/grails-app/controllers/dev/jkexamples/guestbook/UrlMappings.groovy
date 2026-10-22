package dev.jkexamples.guestbook

class UrlMappings {

    static mappings = {
        "/"(controller: 'entry', action: 'index')
        "/sign"(controller: 'entry', action: 'save')
        "/$controller/$action?/$id?(.$format)?"()
        "500"(view: '/error')
        "404"(view: '/notFound')
    }
}
