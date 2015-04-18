/*
 * $Revision$ $Author$ $Date$
 * 
 * Copyright (C) 2008 Arvid Berg <goglepox@users.sf.net>
 * 
 * Contact: cdk-devel@list.sourceforge.net
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package org.openscience.cdk.renderer.elements;

import org.openscience.cdk.annotations.TestClass;
import org.openscience.cdk.annotations.TestMethod;

import java.awt.Color;

/**
 * A text element with added information.
 *
 * @cdk.module renderbasic
 * @cdk.githash
 */
@TestClass("org.openscience.cdk.renderer.elements.AtomSymbolElementTest")
public class AtomSymbolElement extends TextElement {

  /** The formal charge. */
  public final int formalCharge;

  /** The hydrogen count. */
  public final int hydrogenCount;

  /** The hydrogen alignment. */
  public final int alignment;

  @TestMethod("testConstructor")
  public AtomSymbolElement(final double x, final double y, final String symbol,
      final Integer formalCharge, final Integer hydrogenCount,
      final int alignment, final Color color) {
    super(x, y, symbol, color);
    this.formalCharge = formalCharge != null ? formalCharge : -1;
    this.hydrogenCount = hydrogenCount != null ? hydrogenCount : -1;
    this.alignment = alignment;
  }

  /** {@inheritDoc} */
  @Override
  @TestMethod("testAccept")
  public void accept(final IRenderingVisitor v) {
    v.visit(this);
  }

}
