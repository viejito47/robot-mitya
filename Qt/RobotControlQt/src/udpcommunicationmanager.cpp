#include "udpcommunicationmanager.h"
#include <QUdpSocket>

UdpCommunicationManager* UdpCommunicationManager::m_instance = 0;

UdpCommunicationManager::UdpCommunicationManager(QObject *parent) :
    QObject(parent)
{
    m_udpSocket = new QUdpSocket(this);
    m_ip = QHostAddress(Settings::getInstance()->value("ip").toString());
    m_receivePort = Settings::getInstance()->value("udpReceivePort").toString().toInt();
    m_sendPort = Settings::getInstance()->value("udpSendPort").toString().toInt();
    connect(m_udpSocket,SIGNAL(readyRead()),this,SLOT(onReadyRead()));
}

UdpCommunicationManager::~UdpCommunicationManager()
{
    m_udpSocket->close();
    m_udpSocket->deleteLater();
}

void UdpCommunicationManager::closeSocket()
{
    m_udpSocket->close();
}

UdpCommunicationManager* UdpCommunicationManager::getInstance()
{
    if(m_instance == 0)
        m_instance = new UdpCommunicationManager();
    return m_instance;
}

bool UdpCommunicationManager::startListen()
{
    qWarning() << "starting udp..."<<m_receivePort;
    return m_udpSocket->bind(m_receivePort);
}

void UdpCommunicationManager::onReadyRead()
{
    while (m_udpSocket->hasPendingDatagrams()) {
        QByteArray datagram;
        datagram.resize(m_udpSocket->pendingDatagramSize());
        QHostAddress sender;
        quint16 senderPort;

        m_udpSocket->readDatagram(datagram.data(), datagram.size(),
                                &sender, &senderPort);
        qWarning() << "received datagram: " << datagram << " from "<< sender << " "<< senderPort;
        emit datagramReceived(datagram);

    }
}

quint64 UdpCommunicationManager::sendDatagram(QByteArray datagram)
{
   return m_udpSocket->writeDatagram(datagram,m_ip, m_sendPort);
}

