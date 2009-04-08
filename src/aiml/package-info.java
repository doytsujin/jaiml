/*
    jaiml - java AIML library
    Copyright (C) 2004, 2009  Kim Sullivan

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 2 of the License, or
    (at your option) any later version.

    You should have received a copy of the GNU General Public License
    along with this program; if not, write to the Free Software
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

/**
 * <p>Contains all functionality and classes interpret the AIML language. For
 * the end user, these particular classes may be considered the API to use the
 * aiml package:
 * <dl>
 * <dt>{@link aiml.classifier.Classifier aiml.classifier.Classifier}</dt>
 * <dd>The matching/classification functionality</dd>
 * <dt>{@link aiml.classifier.PaternSequence aiml.classifier.PaternSequence}</dt>
 * <dd>Represents a sequence of patterns, or "category context". Create objects of
 * this class to set the conditions for a particular match.</dd>
 * <dt>{@link aiml.context.ContextInfo aiml.context.ContextInfo}</dt>
 * <dd>Maintans information about all known contexts. Encapsulates the "state
 * of the world". You must initialize this structure with enough context sources
 * before adding pattern sequences to the Classifier!</dd>
 * <dt>{@link aiml.context.Context aiml.context.Context}</dt>
 * <dd>Encapsulates a particular state variable used in matching. Inherit this to
 * provide sources of data for your matching environment. </dd>
 * <dt>{@link aiml.bot.Bot aiml.bot.Bot}</dt>
 * <dd>Retains all the information about a particular bot</dd>
 * <dt>{@link aiml.parser.AIMLParser aiml.parser.AIMLParser}</dt>
 * <dd>Reads AIML files into memory and checks for correct syntax</dd>
 * <dt>{@link aiml.script aiml.script.*}</dt>
 * <dd>This package contains all the functionality for parsing and interpreting
 * AIML template side scripts. Unless you want to extend the functionality of the
 * interpreter, you do not need to modify the contents of this package.</dd>
 * </dl>
 * </p>
 * <p>Please be aware that the organization of this library is not yet final, and
 * that in future releases, the package structure and implementation details may
 * change. The basic layout and usage should not.</p>
 */
package aiml;