
if test -n "$CLASSPATH" ; then
  CLASSPATH="/opt/javax-usb/lib/jsr80_ri.jar:${CLASSPATH}"
else
  CLASSPATH="/opt/javax-usb/lib/jsr80_ri.jar"
fi

export CLASSPATH
