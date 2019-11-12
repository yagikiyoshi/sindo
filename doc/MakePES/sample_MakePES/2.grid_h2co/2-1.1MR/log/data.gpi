fname="data"
oname="data.pdf"

set terminal pdf size 5,3
set out oname
#set terminal aqua size 850,600

set tics font "Times New Roman,14"
set xlabel font "Times New Roman,18"
set ylabel font "Times New Roman,18"
set key    font "Times New Roman,18"
set bmargin 5
set lmargin -5

set xrange [-40:40]
set yrange [0:0.2]
set xlabel "Q_5 / bohr(emu)^{1/2}"
set ylabel "V_5 / Hartree"

plot fname using 1:2 w lp title "V_5"
