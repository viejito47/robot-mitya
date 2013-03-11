#include "roboscriptitem.h"

RoboScriptItem::RoboScriptItem(int roboscriptNumber)
{
    init(roboscriptNumber);
}

RoboScriptItem::RoboScriptItem(QString script, int roboscriptNumber)
{
    init(roboscriptNumber);
    m_script = script;
}

RoboScriptItem::RoboScriptItem(QString script, QString playCommand, int roboscriptNumber)
{
   init(roboscriptNumber);
   m_script = script;
   m_playCommand = playCommand;
}

void RoboScriptItem::init(int roboscriptNumber)
{
    m_sent = false;
    m_number = roboscriptNumber;
}
