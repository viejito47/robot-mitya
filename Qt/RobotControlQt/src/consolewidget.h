#ifndef CONSOLEWIDGET_H
#define CONSOLEWIDGET_H

#include <QWidget>

class UdpCommunicationManager;

namespace Ui {
class ConsoleWidget;
}

class ConsoleWidget : public QWidget
{
    Q_OBJECT
    
public:
    explicit ConsoleWidget(QWidget *parent = 0);
    ~ConsoleWidget();
public slots:
    void show();
signals:
    void closed();
private slots:
    void on_sendBtn_clicked();
    void on_exitBtn_clicked();

    void onDatagramReceived(QByteArray datagram);
private:
    Ui::ConsoleWidget *ui;
    UdpCommunicationManager* m_udpManager;
};

#endif // CONSOLEWIDGET_H
