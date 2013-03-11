#include "widget.h"
#include "ui_widget.h"
#include "videoplayer.h"
#include "settings.h"
#include "udpcommunicationmanager.h"
#include <QDebug>

Widget::Widget(QWidget *parent) :
    QWidget(parent),
    ui(new Ui::Widget)
{
    ui->setupUi(this);
    m_settings = Settings::getInstance();
    setSettings();
    connect(&m_watcherWidget,SIGNAL(closed()),this,SLOT(show()));
    connect(&m_console,SIGNAL(closed()),this,SLOT(show()));

}

void Widget::setSettings()
{
    if(m_settings->value("ip","null").toString()=="null")
        m_settings->setValue("ip",ui->ipEdit->text());
    else
        ui->ipEdit->setText(m_settings->value("ip","null").toString());

    if(m_settings->value("camPort","null").toString()=="null")
        m_settings->setValue("camPort",ui->camPort->text());
    else
        ui->camPort->setText(m_settings->value("camPort","null").toString());

    if(m_settings->value("udpReceivePort","null").toString()=="null")
        m_settings->setValue("udpReceivePort",ui->receivePortEdit->text());
    else
        ui->receivePortEdit->setText(m_settings->value("udpReceivePort","null").toString());

    if(m_settings->value("udpSendPort","null").toString()=="null")
        m_settings->setValue("udpSendPort",ui->sendPortEdit->text());
    else
        ui->sendPortEdit->setText(m_settings->value("udpSendPort","null").toString());
}


Widget::~Widget()
{
    delete ui;
    delete m_settings;
}


void Widget::on_ipEdit_editingFinished()
{
    m_settings->setValue("ip",ui->ipEdit->text());
}

void Widget::on_sendPortEdit_editingFinished()
{
    m_settings->setValue("udpSendPort",ui->sendPortEdit->text());
}

void Widget::on_receivePortEdit_editingFinished()
{
    m_settings->setValue("udpReceivePort",ui->receivePortEdit->text());
}

void Widget::on_camPort_editingFinished()
{
    m_settings->setValue("camPort",ui->camPort->text());
}


void Widget::on_controlBtn_clicked()
{
    m_watcherWidget.show();
    m_watcherWidget.openStream(ui->ipEdit->text(),ui->camPort->text());
    this->close();
}

void Widget::on_consoleBtn_clicked()
{
    m_console.show();
    this->close();
}
