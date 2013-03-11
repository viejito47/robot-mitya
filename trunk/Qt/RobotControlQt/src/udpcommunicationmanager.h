#ifndef UPDCOMMUNICATIONMANAGER_H
#define UPDCOMMUNICATIONMANAGER_H

#include <QObject>
#include <QHostAddress>
#include "settings.h"

class QUdpSocket;

class UdpCommunicationManager : public QObject
{
    Q_OBJECT
public:
    static UdpCommunicationManager* getInstance();

    ~UdpCommunicationManager();
    bool startListen();
    void closeSocket();
    quint64 sendDatagram(QByteArray datagram);
signals:
    void datagramReceived(QByteArray datagram);
private slots:
    void onReadyRead();
private:
    static UdpCommunicationManager* m_instance;
    QHostAddress m_ip;
    quint16 m_receivePort;
    quint16 m_sendPort;
    explicit UdpCommunicationManager(QObject *parent = 0);
    QUdpSocket *m_udpSocket;
};

#endif // UPDCOMMUNICATIONMANAGER_H
