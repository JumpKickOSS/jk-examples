<!doctype html>
<html>
<head>
    <meta charset="utf-8">
    <title>Guestbook</title>
    <style>
        body { font: 16px/1.5 system-ui, sans-serif; max-width: 40rem; margin: 2rem auto; padding: 0 1rem; }
        .flash { background: #e6f4ea; padding: .5rem 1rem; }
        .errors { color: #b3261e; }
        li { margin: .5rem 0; }
        small { color: #666; }
    </style>
</head>
<body>
<h1>Guestbook</h1>
<g:if test="${flash.message}"><p class="flash">${flash.message}</p></g:if>
<g:hasErrors bean="${entry}">
    <ul class="errors"><g:eachError bean="${entry}" var="e"><li><g:message error="${e}"/></li></g:eachError></ul>
</g:hasErrors>
<g:form url="/sign" method="POST">
    <p><label>Name <g:textField name="name" value="${entry.name}"/></label></p>
    <p><label>Message<br><g:textArea name="message" value="${entry.message}" rows="3" cols="50"/></label></p>
    <g:submitButton name="sign" value="Sign"/>
</g:form>
<ul>
    <g:each in="${entries}" var="e">
        <li><strong>${e.name}</strong>: ${e.message} <small><g:formatDate date="${e.dateCreated}" format="yyyy-MM-dd HH:mm"/></small></li>
    </g:each>
</ul>
</body>
</html>
