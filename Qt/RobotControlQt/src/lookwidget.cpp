#include "lookwidget.h"
#include "ui_lookwidget.h"
#include <QKeyEvent>
#include <QDebug>
#include "videoplayer.h"
#include "udpcommunicationmanager.h"
#include "settings.h"

LookWidget::LookWidget(QWidget *parent) :
    QWidget(parent),
    ui(new Ui::LookWidget)
{
    ui->setupUi(this);
    m_udpManager = UdpCommunicationManager::getInstance();

    m_videoPlayer = VideoPlayer::getInstance(ui->video);
    connect(m_videoPlayer,SIGNAL(endOfFile()),this,SLOT(onPlayerEndOfFile()));
}

LookWidget::~LookWidget()
{
    delete ui;
    delete m_videoPlayer;

}


void LookWidget::keyPressEvent(QKeyEvent *key)
{
    qWarning() << key->text() << key->key();
    switch(key->key())
    {
        case Qt::Key_F1:
            m_udpManager->sendDatagram(Settings::NORMAL_MOOD.toUtf8());
            break;
        case Qt::Key_F2:
            m_udpManager->sendDatagram(Settings::HAPPY_MOOD.toUtf8());
            break;
        case Qt::Key_F3:
            m_udpManager->sendDatagram(Settings::SADNESS_MOOD.toUtf8());
            break;
        case Qt::Key_F4:
            m_udpManager->sendDatagram(Settings::ANGRY_MOOD.toUtf8());
            break;
        case Qt::Key_F5:
            m_udpManager->sendDatagram(Settings::DISASTER_MOOD.toUtf8());
            break;
        case Qt::Key_Escape:
            m_udpManager->closeSocket();
            this->close();
            emit this->closed();
            break;
    }
}

void LookWidget::onPlayerEndOfFile()
{
    qWarning() << "stream closed";
    m_udpManager->closeSocket();
    this->close();
    emit this->closed();
}

void LookWidget::openStream(QString ip, QString camPort)
{
    QString url = QString("http://%1:%2/videofeed").arg(ip).arg(camPort);
    m_videoPlayer->openUrl(url);
    qWarning() << "listen: " << m_udpManager->startListen();
}
