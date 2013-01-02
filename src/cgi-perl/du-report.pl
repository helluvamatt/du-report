#!/usr/bin/perl
# 
# DU Report - CGI Perl script that generates an HTML report from the 'du' command which lists disk usage.
#
# Copyright (C) 2013 Matt Schneeberger
#
# 
# This program is free software; you can redistribute it and/or
# modify it under the terms of the GNU General Public License
# as published by the Free Software Foundation; either version 2
# of the License, or (at your option) any later version.
#
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
#
# You should have received a copy of the GNU General Public License
# along with this program; if not, write to the Free Software
# Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
#
#
# == NOTE ==: This script cannot currently read the disk usage of files that are not readable by the Apache user
# As a temporary workaround (until a more pormanent solution is found): run the command as sudo and redirect the output to an HTML page:
# user@server $ sudo /path/to/script.pl > /path/to/html/report.html
#

use CGI;
use CGI::Pretty;

# CGI Header
$q = CGI->new;
print $q->header('text/html');

# Error function
sub print_error_html
{
	my $msg = shift;
	$q->start_html("DU Report | Error");
	$q->h1("Error");
	$q->hr;
	$q->p($msg);
	$q->end_html;
	exit;
}

# Constants
use constant UNIT_DIV => 1024;

# Variables
$du = "/usr/bin/du";
$du_args = "--max-depth=1 --bytes --all";
@units = ( "B", "K", "M", "G", "T", "P", "Y" );
$__SELF__ = $q->url();

# Directory
$top_dir = "/";
$dir = $top_dir . "/" . $q->param('d');

# Run report
%report = map { /^(\d+)\t(.+)$/ , $1 => $2 } `$du $du_args $dir`;

# Sort Output
@sorted_report = sort { $b <=> $a } keys %report ;

# Print HTML Header
print $q->start_html("DU Report | $dir");
print $q->h1($dir);
print $q->hr;

# Print HTML report
@table_rows = ();
MAIN_LOOP:
foreach $key (@sorted_report)  #while ( my($size, $path) = each %report )
{
	$size = $key;
	$path = $report{$key};
	$size_unit = 0;
	FORMATTER_LOOP:
	until ( $size < UNIT_DIV )
	{
		last FORMATTER_LOOP unless ( $size_unit < $#units );
		$size /= UNIT_DIV;
		$size_unit++;
	}

	push(
		@table_rows ,
		$q->Tr(
			$q->td(
				{ -align => "right" } ,
				$q->span(
					sprintf("%.3f", $size ) ,
					$q->b(
						$units[$size_unit]
					)
				)
			) ,
			$q->td(
				$q->a(
					{ -href => "${__SELF__}?d=$path" } ,
					$path
				)
			)
		)
	); 
}
print $q->table({ -border=>"solid 1px black" }, @table_rows );

# Print HTML footer
print $q->end_html;
