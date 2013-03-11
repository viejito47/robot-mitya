#ifndef SETTINGS_H
#define SETTINGS_H

#include <QSettings>


class Settings : public QSettings
{
    Q_OBJECT
public:

    static Settings* getInstance();
    void setValue(const QString &key, const QVariant &value);

    static const QString NORMAL_MOOD; //норм настроение
    static const QString HAPPY_MOOD; // счастливое настроение
    static const QString SADNESS_MOOD; // грустное настроение
    static const QString ANGRY_MOOD; // злое настроение
    static const QString DISASTER_MOOD; // убитое настроение

signals:
    void newValue(QString key, QString newVal);
public slots:

private:
    explicit Settings(QObject *parent = 0);
    static Settings* m_instance;


};

#endif // SETTINGS_H
