#include "consolewidget.h"
#include "ui_consolewidget.h"
#include "udpcommunicationmanager.h"

ConsoleWidget::ConsoleWidget(QWidget *parent) :
    QWidget(parent),
    ui(new Ui::ConsoleWidget)
{
    ui->setupUi(this);

}

void ConsoleWidget::onDatagramReceived(QByteArray datagram)
{
    ui->commandHistory->setTextColor(Qt::red);
    ui->commandHistory->append(datagram);
}

ConsoleWidget::~ConsoleWidget()
{
    delete ui;
    delete m_udpManager;
}

void ConsoleWidget::on_sendBtn_clicked()
{
    ui->commandHistory->setTextColor(Qt::black);
    if(m_udpManager->sendDatagram(ui->commandEdit->text().toUtf8()))
        ui->commandHistory->append(ui->commandEdit->text());
    else
        ui->commandHistory->append("Сообщение не доставлено!");
}

void ConsoleWidget::on_exitBtn_clicked()
{
    disconnect(m_udpManager,SIGNAL(datagramReceived(QByteArray)),this,SLOT(onDatagramReceived(QByteArray)));
    m_udpManager->closeSocket();
    this->close();
    emit closed();
}

void ConsoleWidget::show()
{
    m_udpManager = UdpCommunicationManager::getInstance();
    connect(m_udpManager,SIGNAL(datagramReceived(QByteArray)),this,SLOT(onDatagramReceived(QByteArray)));
    qWarning() << "listen: " << m_udpManager->startListen();
    QWidget::show();
}
