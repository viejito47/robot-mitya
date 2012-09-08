#include "RoboScript.h"
extern "C" {
#include <string.h>
#include <stdlib.h>
}

RoboScript::RoboScript()
{
}

signed short RoboScript::initialize(int actionsCount)
{
	this->finalize();
	this->actionsCount = 0;
	this->actions = (RoboAction *)calloc(actionsCount, sizeof(RoboAction));
	if (this->actions == NULL)
	{
		return ROBOSCRIPT_ERROR_CANNOT_ALLOC_MEMORY;
	}
	
	this->actionsMaxCount = actionsCount;
	return ROBOSCRIPT_OK;
}

bool RoboScript::getIsInitialized()
{
	return (this->actionsMaxCount > 0);
}

void RoboScript::finalize()
{
	this->clear();
	if (this->actions != NULL)
	{
		free(this->actions);
		this->actions = NULL;
	}
	this->actionsMaxCount = 0;
}

signed short RoboScript::addAction(RoboAction action)
{
	if (!this->getIsInitialized())
	{
		return ROBOSCRIPT_ERROR_NOT_INITIALIZED;
	}
	
	if (this->actionsCount >= this->actionsMaxCount)
	{
		return ROBOSCRIPT_ERROR_ARRAY_IS_OVERLOADED;
	}

	this->actions[this->actionsCount] = action;
	this->actionsCount++;
	
	return ROBOSCRIPT_OK;
}

int RoboScript::getActionsCount()
{
	return this->actionsCount;
}

signed short RoboScript::getActionAt(int position, RoboAction &action)
{
	if (!this->getIsInitialized())
	{
		return ROBOSCRIPT_ERROR_NOT_INITIALIZED;
	}
	
	if ((position < 0) || (position >= this->actionsCount))
	{
		return ROBOSCRIPT_ERROR_OUT_OF_BOUNDS;
	}
	
	action = this->actions[position];
	return ROBOSCRIPT_OK;
}

void RoboScript::clear()
{
	this->isExecuting = false;
	this->actionsCount = 0;
}

void RoboScript::startExecution()
{
	this->startMillis = millis();
	this->currentPosition = 0;
	this->nextCommandMillis = 0;
	this->isExecuting = true;
}

void RoboScript::stopExecution()
{
	this->isExecuting = false;
}

bool RoboScript::hasActionToExecute(String &command, int &value)
{
	if (this->actionsCount == 0)
	{
		return false;
	}

	if (!this->isExecuting)
	{
		return false;
	}
	
	if (this->currentPosition >= this->actionsCount)
	{
		this->isExecuting = false;
		return false;
	}
	
	unsigned int currentMillis = millis() - this->startMillis;
	if (currentMillis >= this->nextCommandMillis)
	{
		RoboAction currentAction = this->actions[this->currentPosition];
		this->currentPosition++;

		command = String((char)currentAction.Command);
		value = currentAction.Value;
		this->nextCommandMillis += currentAction.Delay;
		
		return true;
	}
	
	return false;
}
