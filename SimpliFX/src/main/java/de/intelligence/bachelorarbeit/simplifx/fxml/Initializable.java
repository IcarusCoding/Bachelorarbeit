/*
 * Copyright (c) 2010, 2017, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

package de.intelligence.bachelorarbeit.simplifx.fxml;

import java.net.URL;

import de.intelligence.bachelorarbeit.simplifx.localization.II18N;

/**
 * Controller initialization interface.
 * <p>
 * <em>NOTE</em> This interface has been superseded by automatic injection of
 * <code>location</code> and <code>resources</code> properties into the
 * controller. {@link SimpliFXMLLoader} will now automatically call any suitably
 * annotated no-arg <code>initialize()</code> method defined by the controller.
 * It is recommended that the injection approach be used whenever possible.
 * <p>
 * This class was copied and is a modified version of the {@link javafx.fxml.Initializable} class.
 *
 * @since JavaFX 2.0
 */
public interface Initializable {
    /**
     * Called to initialize a controller after its root element has been
     * completely processed.
     *
     * @param location The location used to resolve relative paths for the root object, or
     *                 {@code null} if the location is not known.
     * @param ii18N    The {@link II18N} instance used to localize the root object, or {@code null} if
     *                 the root object was not localized.
     */
    void initialize(URL location, II18N ii18N);
}
