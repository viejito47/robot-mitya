#include "settings.h"

Settings* Settings::m_instance = 0;

const QString Settings::NORMAL_MOOD = "M0001";
const QString Settings::HAPPY_MOOD = "M0002";
const QString Settings::SADNESS_MOOD = "M0003";
const QString Settings::ANGRY_MOOD = "M0004";
const QString Settings::DISASTER_MOOD = "M0005";


Settings::Settings(QObject *parent) :
    QSettings(parent)
{

}

Settings* Settings::getInstance()
{
    if(m_instance == 0)
        m_instance = new Settings();
    return m_instance;
}


void Settings::setValue(const QString &key, const QVariant &value)
{
    QSettings::setValue(key,value);
    emit newValue(key,value.toString());
}
