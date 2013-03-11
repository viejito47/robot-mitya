#include "videoplayer.h"
#include <vlc-qt/Instance.h>
#include <vlc-qt/Media.h>
#include <vlc-qt/WidgetVideo.h>
#include <vlc-qt/Common.h>
#include <QDebug>

VideoPlayer* VideoPlayer::m_instance = 0;

VideoPlayer::VideoPlayer(VlcWidgetVideo* output, QObject *parent) :
    QObject(parent)
{
    m_vlcinstance = new VlcInstance(VlcCommon::args(), this);
    m_player = new VlcMediaPlayer(m_vlcinstance);
    setOutput(output);
    connect(m_player,SIGNAL(positionChanged(float)),this,SIGNAL(positionChanged(float)));
    connect(m_player,SIGNAL(paused()),this,SIGNAL(paused()));
    connect(m_player,SIGNAL(stopped()),this,SIGNAL(stopped()));
    connect(m_player,SIGNAL(stateChanged()),this,SLOT(onStateChanged()));
    connect(m_player,SIGNAL(error()),this,SIGNAL(error()));
    connect(m_player,SIGNAL(end()),this,SIGNAL(endOfFile()));
}

void VideoPlayer::onStateChanged()
{
    emit stateChanged(m_player->state());
}

VideoPlayer* VideoPlayer::getInstance(VlcWidgetVideo *output)
{
    if(m_instance == 0)
        m_instance = new VideoPlayer(output);
    else
        m_instance->setOutput(output);
    return m_instance;
}

void VideoPlayer::setOutput(VlcWidgetVideo *output)
{
    if(output != NULL)
    {
        m_player->setVideoWidget(output);
        output->setMediaPlayer(m_player);
    }
}

void VideoPlayer::openUrl(QString url)
{
    qWarning()<<url;
    m_media = m_player->currentMedia();
    if(m_media!= NULL)
    {
        m_media->deleteLater();
        m_media = NULL;
    }
    m_media = new VlcMedia(url,m_vlcinstance);
    m_player->open(m_media);

}


void VideoPlayer::stop()
{
     m_player->stop();
}


