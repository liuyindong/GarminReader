
if ( ${?CLASSPATH} ) then
  setenv CLASSPATH "/opt/javax-usb/lib/jsr80_ri.jar:${CLASSPATH}"
else
  setenv CLASSPATH "/opt/javax-usb/lib/jsr80_ri.jar"
fi
