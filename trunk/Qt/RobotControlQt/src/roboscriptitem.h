#ifndef ROBOSCRIPTITEM_H
#define ROBOSCRIPTITEM_H
#include <QString>

class RoboScriptItem
{
public:
    RoboScriptItem(int roboscriptNumber);
    RoboScriptItem(QString script, int roboscriptNumber);
    RoboScriptItem(QString script, QString playCommand, int roboscriptNumber);

    bool isEmpty(){return m_script.isEmpty();}
    bool wasSent(){return m_sent;}

    void setSent(bool sent){ m_sent = sent;}
    void setScript(QString script){m_script = script;}

private:
    void init(int roboscriptNumber);
    QString m_playCommand;
    QString m_script;
    bool m_sent;
    int m_number;

};

#endif // ROBOSCRIPTITEM_H
