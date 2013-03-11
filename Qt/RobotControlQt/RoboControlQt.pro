#-------------------------------------------------
#
# Project created by QtCreator 2013-03-07T15:26:10
#
#-------------------------------------------------

QT       += core gui network

TARGET = RoboControlQt
TEMPLATE = app



LIBS        +=  -lvlc-qt -lvlc-qt-widgets

OBJECTS_DIR = obj
MOC_DIR = obj

HEADERS += \
    src/widget.h \
    src/videoplayer.h \
    src/udpcommunicationmanager.h \
    src/settings.h \
    src/lookwidget.h \
    src/consolewidget.h \
    src/roboscriptitem.h

SOURCES += \
    src/widget.cpp \
    src/videoplayer.cpp \
    src/udpcommunicationmanager.cpp \
    src/settings.cpp \
    src/main.cpp \
    src/lookwidget.cpp \
    src/consolewidget.cpp \
    src/roboscriptitem.cpp

FORMS += \
    src/widget.ui \
    src/lookwidget.ui \
    src/consolewidget.ui

UI_HEADERS_DIR += src
