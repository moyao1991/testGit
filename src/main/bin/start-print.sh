#!/bin/sh

#set -e

print_file=""

if [ ! $1 ]; then
	echo "Please input file..."
	exit 1
fi

if [ ! -s $1 ]; then
	print_file=$(pwd)/$1	
	echo $print_file
else
	print_file=$1
	echo $1
fi	

tmp_doc=/opt/tmp/print_tmp
if [ ! -d $tmp_doc ]; then
	sudo mkdir $tmp_doc 
	sudo chmod 777 $tmp_doc
fi
tmp_pdf=$tmp_doc/$3.pdf
tmp_ps=$tmp_doc/$3.ps
tmp_ras=$tmp_doc/$3.ras
tmp_out=$tmp_doc/$3.tmp

echo $2
echo 1

is_epson=$(echo $2 | grep -i epson)
is_hp=$(echo $2 | grep -i hp)
is_nfcp=$(echo $2 | grep -i nfcp)
is_zhongying=$(echo $2 | grep -i zhongying)
is_canon=$(echo $2 | grep -i canon)
is_sprt=$(echo $2 | grep -i sprt)
is_dascom=$(echo $2 | grep -i dascom)
is_oki=$(echo $2 | grep -i oki)
is_deli=$(echo $2 | grep -i deli)

driver_path=$(dirname $0)
driver_path+=/driver
echo $driver_path
if [ $is_epson ]; then
	export PPD=$driver_path/epson/$2
elif [ $is_zhongying ]; then
	export PPD=$driver_path/zhongying/$2
elif [ $is_nfcp ]; then
	export PPD=$driver_path/fujitsu/$2
elif [ $is_hp ]; then
	export PPD=$driver_path/hp/$2
elif [ $is_canon ]; then
	export PPD=$driver_path/canon/$2
elif [ $is_sprt ]; then
	export PPD=$driver_path/sprt/$2
elif [ $is_dascom ]; then
	export PPD=$driver_path/dascom/$2
elif [ $is_oki ]; then
	export PPD=$driver_path/oki/$2
elif [ $is_deli ]; then
	export PPD=$driver_path/deli/$2
fi
echo $PPD

exe_path=$(dirname $0)
exe_path+=/plugin
result=-1
if [ $is_canon ]; then
	echo "This CANON printer"
	$exe_path/pdftops 1 "root" "" 1 1 $PPD $print_file >> $tmp_ps
	$exe_path/pstocanonij 1 "root" "" 1 1 $PPD $tmp_ps >> $tmp_out
else 	
	#echo "Start pdftopdf......"
	#$exe_path/pdftopdf 1 "root" "" 1 "PageSize=Selfsize1" $PPD $print_file >> $tmp_pdf
	echo "Start gstoraster......"
	$exe_path/gstoraster 1 "root" "" 1 1 $PPD $print_file >> $tmp_ras
	if [ $is_epson ]; then
		echo "This EPSON printer"
		$exe_path/rastertoepson 1 "root" "" 1 1 $PPD $tmp_ras >> $tmp_out
	elif [ $is_zhongying ]; then
		echo "This ZhongYing printer"
		$exe_path/rastertoepson 1 "root" "" 1 "Pagesize=2dfire" $PPD $tmp_ras >> $tmp_out 
	elif [ $is_nfcp ]; then
		echo "This nanjing fujitsu printer"
		$exe_path/rastertoepson 1 "root" "" 1 "PageSize=2dfire" $PPD $tmp_ras >> $tmp_out 
	elif [ $is_sprt ]; then
		echo "This SPRT printer"
		$exe_path/rastertosprt 1 "root" "" 1 1 $PPD $tmp_ras >> $tmp_out 
	elif [ $is_dascom ]; then
		echo "This DASCOM printer"
		$exe_path/rastertoepson 1 "root" "" 1 "PageSize=2dfire" $PPD $tmp_ras >> $tmp_out 
	elif [ $is_oki ]; then
		echo "This OKI printer"
		$exe_path/rastertoepson 1 "root" "" 1 "PageSize=2dfire" $PPD $tmp_ras >> $tmp_out 
	elif [ $is_deli ]; then
		echo "This DELI printer"
		$exe_path/rastertoepson 1 "root" "" 1 "PageSize=2dfire" $PPD $tmp_ras >> $tmp_out 
	elif [ $is_hp ]; then
		echo "This HP printer"
		$exe_path/hpcups 1 "root" "" 1 1 $PPD $tmp_ras >> $tmp_out 
	fi
fi

if [ -e $tmp_ps ]; then 
	rm $tmp_ps
fi

if [ -e $tmp_ras ]; then
	rm $tmp_ras;
fi

if [ -e $tmp_pdf ]; then
	rm $tmp_pdf;
fi

echo $result 
echo $1 $2

