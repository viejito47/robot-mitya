#include "widget.h"
#include <QApplication>

int main(int argc, char *argv[])
{
    QApplication a(argc, argv);
    QCoreApplication::setOrganizationName("RoboControlQt");
    Widget w;
    w.show();
    
    return a.exec();
}
