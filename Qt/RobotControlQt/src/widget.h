#ifndef WIDGET_H
#define WIDGET_H

#include <QWidget>
#include "lookwidget.h"
#include "consolewidget.h"


class Settings;

namespace Ui {
class Widget;
}

class Widget : public QWidget
{
    Q_OBJECT
    
public:
    explicit Widget(QWidget *parent = 0);
    ~Widget();

private slots:

    void on_ipEdit_editingFinished();
    void on_sendPortEdit_editingFinished();
    void on_receivePortEdit_editingFinished();
    void on_camPort_editingFinished();

    void on_controlBtn_clicked();

    void on_consoleBtn_clicked();

private:
    void setSettings();

    Ui::Widget *ui;
    Settings* m_settings;
    LookWidget m_watcherWidget;
    ConsoleWidget m_console;
};

#endif // WIDGET_H
