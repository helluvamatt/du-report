DU Report - Socket Server
=========================

This will be the directory for the simple socket server for DU Report

The idea here is to get around the security limitations by running this server as the superuser (ROOT) and the CGI script asking the server for the DU data from the server over a socket. The socket then responds with the output of the command. The Perl script can then sort the data and format it as HTML.

This means that this socket server can be very simple, however, it should be multi-threaded (as Apache is multithreaded, we don't require state data, and we are just calling `du` on the system.)
