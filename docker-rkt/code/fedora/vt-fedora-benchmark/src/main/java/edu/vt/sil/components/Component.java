package edu.vt.sil.components;

import edu.vt.sil.administrator.AdministratorCommand;

/**
 * Author: dedocibula
 * Created on: 29.2.2016.
 */
public interface Component {
    String showLabel(AdministratorCommand command);

    void handleCommand(AdministratorCommand command, String[] arguments) throws Exception;
}
