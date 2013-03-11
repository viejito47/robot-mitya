#ifndef VIDEOPLAYER_H
#define VIDEOPLAYER_H

#include <QObject>
#include <QString>
#include <vlc-qt/MediaPlayer.h>



class VlcWidgetVideo;
class VlcInstance;
class VlcMedia;


class VideoPlayer : public QObject
{
    Q_OBJECT
public:
    static VideoPlayer* getInstance(VlcWidgetVideo* output);

    void setOutput(VlcWidgetVideo* output);
    void stop();
    void openUrl(QString url);
signals:
    void error();
    void positionChanged(float position);
    void paused();
    void stopped();
    void endOfFile();
    void stateChanged(Vlc::State);

private slots:
    void onStateChanged();

private:
    VideoPlayer(VlcWidgetVideo* output, QObject *parent = 0);
    VlcInstance *m_vlcinstance;
    VlcMedia *m_media;
    VlcMediaPlayer *m_player;
    static VideoPlayer* m_instance;
};

#endif // VIDEOPLAYER_H
