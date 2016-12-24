package edu.vt.sil.components;

import edu.vt.sil.administrator.AdministratorCommand;

/**
 * Author: dedocibula
 * Created on: 29.2.2016.
 */
public abstract class AbstractComponent implements Component {
    @Override
    public void handleCommand(AdministratorCommand command, String[] arguments) throws Exception {
        prepare(command, arguments);
        execute();
    }

    protected abstract void prepare(AdministratorCommand command, String[] arguments) throws Exception;

    protected abstract void execute() throws Exception;
}
