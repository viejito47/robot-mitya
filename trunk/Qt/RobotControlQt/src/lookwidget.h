#ifndef LOOKWIDGET_H
#define LOOKWIDGET_H

#include <QWidget>

class UdpCommunicationManager;
class VideoPlayer;
namespace Ui {
class LookWidget;
}

class LookWidget : public QWidget
{
    Q_OBJECT
    
public:
    void openStream(QString ip, QString camPort);
    explicit LookWidget(QWidget *parent = 0);
    ~LookWidget();
signals:
    void closed();
private slots:
     void onPlayerEndOfFile();

protected:
     void keyPressEvent(QKeyEvent *key);
private:
    Ui::LookWidget *ui;
    UdpCommunicationManager* m_udpManager;
    VideoPlayer* m_videoPlayer;
};

#endif // LOOKWIDGET_H
