# Gateway (WIP)
This project is just an experiment. Please do not use it in produktion.

Complete Project description coming soon...

## tl;dr
|HttpRequest from client |HttpResponse to client |
| :------------ | :------------ |
|↓ Header (and body?) manipulation/sanitization **(Filter)** |↑ Header manipulation/sanitization **(Filter)**|
|↓ Determine destination URL **(Servlet)** |↑ Rewrite URLs in body and header to the gateway endpoint **(Servlet)**|
|↓ Request destination and receive response **(Servlet)**|↑ Determine character encoding for target response body **(Servlet)**|

# Build
mvn package

# RUN
