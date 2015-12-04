package com.useroptix.odtool.components;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.FocusListener;
import java.awt.geom.Point2D;
import java.beans.PropertyChangeListener;

import javax.swing.CellRendererPane;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.MouseInputListener;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicListUI;

import sun.swing.DefaultLookup;

public class UOListUI extends javax.swing.plaf.basic.BasicListUI {
    private static final StringBuilder BASELINE_COMPONENT_KEY =
            new StringBuilder("List.baselineComponent");

        protected CellRendererPane rendererPane;

        // Listeners that this UI attaches to the JList
        protected FocusListener focusListener;
        protected MouseInputListener mouseInputListener;
        protected ListSelectionListener listSelectionListener;
        protected ListDataListener listDataListener;
        protected PropertyChangeListener propertyChangeListener;

        protected int[] cellHeights = null;
        protected int cellHeight = -1;
        protected int cellWidth = -1;
        protected int updateLayoutStateNeeded = modelChanged;
        /**
         * Height of the list. When asked to paint, if the current size of
         * the list differs, this will update the layout state.
         */
        private int listHeight;

        /**
         * Width of the list. When asked to paint, if the current size of
         * the list differs, this will update the layout state.
         */
        private int listWidth;

        /**
         * The layout orientation of the list.
         */
        private int layoutOrientation;

        // Following ivars are used if the list is laying out horizontally

        /**
         * Number of columns to create.
         */
        private int columnCount;
        /**
         * Preferred height to make the list, this is only used if the
         * the list is layed out horizontally.
         */
        private int preferredHeight;
        /**
         * Number of rows per column. This is only used if the row height is
         * fixed.
         */
        private int rowsPerColumn;

        /**
         * The time factor to treate the series of typed alphanumeric key
         * as prefix for first letter navigation.
         */
        private long timeFactor = 1000L;

        /**
         * Local cache of JList's client property "List.isFileList"
         */
        private boolean isFileList = false;

        /**
         * Local cache of JList's component orientation property
         */
        private boolean isLeftToRight = true;

        /* The bits below define JList property changes that affect layout.
         * When one of these properties changes we set a bit in
         * updateLayoutStateNeeded.  The change is dealt with lazily, see
         * maybeUpdateLayoutState.  Changes to the JLists model, e.g. the
         * models length changed, are handled similarly, see DataListener.
         */

        protected final static int modelChanged = 1 << 0;
        protected final static int selectionModelChanged = 1 << 1;
        protected final static int fontChanged = 1 << 2;
        protected final static int fixedCellWidthChanged = 1 << 3;
        protected final static int fixedCellHeightChanged = 1 << 4;
        protected final static int prototypeCellValueChanged = 1 << 5;
        protected final static int cellRendererChanged = 1 << 6;
        private final static int layoutOrientationChanged = 1 << 7;
        private final static int heightChanged = 1 << 8;
        private final static int widthChanged = 1 << 9;
        private final static int componentOrientationChanged = 1 << 10;

        private static final int DROP_LINE_THICKNESS = 2;


        /**
         * Paint one List cell: compute the relevant state, get the "rubber stamp"
         * cell renderer component, and then use the CellRendererPane to paint it.
         * Subclasses may want to override this method rather than paint().
         *
         * @see #paint
         */
        protected void paintCell(
            Graphics g,
            int row,
            Rectangle rowBounds,
            ListCellRenderer cellRenderer,
            ListModel dataModel,
            ListSelectionModel selModel,
            int leadIndex)
        {
            Object value = dataModel.getElementAt(row);
            boolean cellHasFocus = list.hasFocus() && (row == leadIndex);
            boolean isSelected = selModel.isSelectedIndex(row);

            Component rendererComponent =
                cellRenderer.getListCellRendererComponent(list, value, row, isSelected, cellHasFocus);

            int cx = rowBounds.x;
            int cy = rowBounds.y;
            int cw = rowBounds.width;
            int ch = rowBounds.height;

            if (isFileList) {
                // Shrink renderer to preferred size. This is mostly used on Windows
                // where selection is only shown around the file name, instead of
                // across the whole list cell.
                int w = Math.min(cw, rendererComponent.getPreferredSize().width + 4);
                if (!isLeftToRight) {
                    cx += (cw - w);
                }
                cw = w;
            }

            rendererPane.paintComponent(g, rendererComponent, list, cx, cy, cw, ch, true);
        }


        /**
         * Paint the rows that intersect the Graphics objects clipRect.  This
         * method calls paintCell as necessary.  Subclasses
         * may want to override these methods.
         *
         * @see #paintCell
         */
        public void paint(Graphics g, JComponent c) {
            Shape clip = g.getClip();
            paintImpl(g, c);
            g.setClip(clip);

            paintDropLine(g);
        }

        private void paintImpl(Graphics g, JComponent c)
        {
            switch (layoutOrientation) {
            case JList.VERTICAL_WRAP:
                if (list.getHeight() != listHeight) {
                    updateLayoutStateNeeded |= heightChanged;
                    redrawList();
                }
                break;
            case JList.HORIZONTAL_WRAP:
                if (list.getWidth() != listWidth) {
                    updateLayoutStateNeeded |= widthChanged;
                    redrawList();
                }
                break;
            default:
                break;
            }
            maybeUpdateLayoutState();

            ListCellRenderer renderer = list.getCellRenderer();
            ListModel dataModel = list.getModel();
            ListSelectionModel selModel = list.getSelectionModel();
            int size;

            if ((renderer == null) || (size = dataModel.getSize()) == 0) {
                return;
            }

            // Determine how many columns we need to paint
            Rectangle paintBounds = g.getClipBounds();

            int startColumn, endColumn;
            if (c.getComponentOrientation().isLeftToRight()) {
                startColumn = convertLocationToColumn(paintBounds.x,
                                                      paintBounds.y);
                endColumn = convertLocationToColumn(paintBounds.x +
                                                    paintBounds.width,
                                                    paintBounds.y);
            } else {
                startColumn = convertLocationToColumn(paintBounds.x +
                                                    paintBounds.width,
                                                    paintBounds.y);
                endColumn = convertLocationToColumn(paintBounds.x,
                                                      paintBounds.y);
            }
            int maxY = paintBounds.y + paintBounds.height;
            int leadIndex = adjustIndex(list.getLeadSelectionIndex(), list);
            int rowIncrement = (layoutOrientation == JList.HORIZONTAL_WRAP) ?
                               columnCount : 1;


            for (int colCounter = startColumn; colCounter <= endColumn;
                 colCounter++) {
                // And then how many rows in this columnn
                int row = convertLocationToRowInColumn(paintBounds.y, colCounter);
                int rowCount = getRowCount(colCounter);
                int index = getModelIndex(colCounter, row);
                Rectangle rowBounds = getCellBounds(list, index, index);

                if (rowBounds == null) {
                    // Not valid, bail!
                    return;
                }
                while (row < rowCount && rowBounds.y < maxY &&
                       index < size) {
                    rowBounds.height = getHeight(colCounter, row);
                    g.setClip(rowBounds.x, rowBounds.y, rowBounds.width,
                              rowBounds.height);
                    g.clipRect(paintBounds.x, paintBounds.y, paintBounds.width,
                               paintBounds.height);
                    paintCell(g, index, rowBounds, renderer, dataModel, selModel,
                              leadIndex);
                    rowBounds.y += rowBounds.height;
                    index += rowIncrement;
                    row++;
                }
            }
            // Empty out the renderer pane, allowing renderers to be gc'ed.
            rendererPane.removeAll();
        }

        private void paintDropLine(Graphics g) {
            JList.DropLocation loc = list.getDropLocation();
            if (loc == null || !loc.isInsert()) {
                return;
            }

            Color c = DefaultLookup.getColor(list, this, "List.dropLineColor", null);
            if (c != null) {
                g.setColor(c);
                Rectangle rect = getDropLineRect(loc);
                g.fillRoundRect(rect.x, rect.y, rect.width, rect.height, 55,55);
            }
        }

        private Rectangle getDropLineRect(JList.DropLocation loc) {
            int size = list.getModel().getSize();

            if (size == 0) {
                Insets insets = list.getInsets();
                if (layoutOrientation == JList.HORIZONTAL_WRAP) {
                    if (isLeftToRight) {
                        return new Rectangle(insets.left, insets.top, DROP_LINE_THICKNESS, 20);
                    } else {
                        return new Rectangle(list.getWidth() - DROP_LINE_THICKNESS - insets.right,
                                             insets.top, DROP_LINE_THICKNESS, 20);
                    }
                } else {
                    return new Rectangle(insets.left, insets.top,
                                         list.getWidth() - insets.left - insets.right,
                                         DROP_LINE_THICKNESS);
                }
            }

            Rectangle rect = null;
            int index = loc.getIndex();
            boolean decr = false;

            if (layoutOrientation == JList.HORIZONTAL_WRAP) {
                if (index == size) {
                    decr = true;
                } else if (index != 0 && convertModelToRow(index)
                                             != convertModelToRow(index - 1)) {

                    Rectangle prev = getCellBounds(list, index - 1);
                    Rectangle me = getCellBounds(list, index);
                    Point p = loc.getDropPoint();

                    if (isLeftToRight) {
                        decr = Point2D.distance(prev.x + prev.width,
                                                prev.y + (int)(prev.height / 2.0),
                                                p.x, p.y)
                               < Point2D.distance(me.x,
                                                  me.y + (int)(me.height / 2.0),
                                                  p.x, p.y);
                    } else {
                        decr = Point2D.distance(prev.x,
                                                prev.y + (int)(prev.height / 2.0),
                                                p.x, p.y)
                               < Point2D.distance(me.x + me.width,
                                                  me.y + (int)(prev.height / 2.0),
                                                  p.x, p.y);
                    }
                }

                if (decr) {
                    index--;
                    rect = getCellBounds(list, index);
                    if (isLeftToRight) {
                        rect.x += rect.width;
                    } else {
                        rect.x -= DROP_LINE_THICKNESS;
                    }
                } else {
                    rect = getCellBounds(list, index);
                    if (!isLeftToRight) {
                        rect.x += rect.width - DROP_LINE_THICKNESS;
                    }
                }

                if (rect.x >= list.getWidth()) {
                    rect.x = list.getWidth() - DROP_LINE_THICKNESS;
                } else if (rect.x < 0) {
                    rect.x = 0;
                }

                rect.width = DROP_LINE_THICKNESS;
            } else if (layoutOrientation == JList.VERTICAL_WRAP) {
                if (index == size) {
                    index--;
                    rect = getCellBounds(list, index);
                    rect.y += rect.height;
                } else if (index != 0 && convertModelToColumn(index)
                                             != convertModelToColumn(index - 1)) {

                    Rectangle prev = getCellBounds(list, index - 1);
                    Rectangle me = getCellBounds(list, index);
                    Point p = loc.getDropPoint();
                    if (Point2D.distance(prev.x + (int)(prev.width / 2.0),
                                         prev.y + prev.height,
                                         p.x, p.y)
                            < Point2D.distance(me.x + (int)(me.width / 2.0),
                                               me.y,
                                               p.x, p.y)) {

                        index--;
                        rect = getCellBounds(list, index);
                        rect.y += rect.height;
                    } else {
                        rect = getCellBounds(list, index);
                    }
                } else {
                    rect = getCellBounds(list, index);
                }

                if (rect.y >= list.getHeight()) {
                    rect.y = list.getHeight() - DROP_LINE_THICKNESS;
                }

                rect.height = DROP_LINE_THICKNESS;
            } else {
                if (index == size) {
                    index--;
                    rect = getCellBounds(list, index);
                    rect.y += rect.height;
                } else {
                    rect = getCellBounds(list, index);
                }

                if (rect.y >= list.getHeight()) {
                    rect.y = list.getHeight() - DROP_LINE_THICKNESS;
                }

                rect.height = DROP_LINE_THICKNESS;
            }

            return rect;
        }

        /**
         * Returns the baseline.
         *
         * @throws NullPointerException {@inheritDoc}
         * @throws IllegalArgumentException {@inheritDoc}
         * @see javax.swing.JComponent#getBaseline(int, int)
         * @since 1.6
         */
        public int getBaseline(JComponent c, int width, int height) {
            super.getBaseline(c, width, height);
            int rowHeight = list.getFixedCellHeight();
            UIDefaults lafDefaults = UIManager.getLookAndFeelDefaults();
            Component renderer = (Component)lafDefaults.get(
                    BASELINE_COMPONENT_KEY);
            if (renderer == null) {
                ListCellRenderer lcr = (ListCellRenderer)UIManager.get(
                        "List.cellRenderer");

                // fix for 6711072 some LAFs like Nimbus do not provide this
                // UIManager key and we should not through a NPE here because of it
                if (lcr == null) {
                    lcr = new DefaultListCellRenderer();
                }
                renderer = lcr.getListCellRendererComponent(
                        list, "a", -1, false, false);
                lafDefaults.put(BASELINE_COMPONENT_KEY, renderer);
            }
            renderer.setFont(list.getFont());
            // JList actually has much more complex behavior here.
            // If rowHeight != -1 the rowHeight is either the max of all cell
            // heights (layout orientation != VERTICAL), or is variable depending
            // upon the cell.  We assume a default size.
            // We could theoretically query the real renderer, but that would
            // not work for an empty model and the results may vary with
            // the content.
            if (rowHeight == -1) {
                rowHeight = renderer.getPreferredSize().height;
            }
            return renderer.getBaseline(Integer.MAX_VALUE, rowHeight) +
                    list.getInsets().top;
        }

        /**
         * Returns an enum indicating how the baseline of the component
         * changes as the size changes.
         *
         * @throws NullPointerException {@inheritDoc}
         * @see javax.swing.JComponent#getBaseline(int, int)
         * @since 1.6
         */
        public Component.BaselineResizeBehavior getBaselineResizeBehavior(
                JComponent c) {
            super.getBaselineResizeBehavior(c);
            return Component.BaselineResizeBehavior.CONSTANT_ASCENT;
        }

        /**
         * The preferredSize of the list depends upon the layout orientation.
         * <table summary="Describes the preferred size for each layout orientation">
         * <tr><th>Layout Orientation</th><th>Preferred Size</th></tr>
         * <tr>
         *   <td>JList.VERTICAL
         *   <td>The preferredSize of the list is total height of the rows
         *       and the maximum width of the cells.  If JList.fixedCellHeight
         *       is specified then the total height of the rows is just
         *       (cellVerticalMargins + fixedCellHeight) * model.getSize() where
         *       rowVerticalMargins is the space we allocate for drawing
         *       the yellow focus outline.  Similarly if fixedCellWidth is
         *       specified then we just use that.
         *   </td>
         * <tr>
         *   <td>JList.VERTICAL_WRAP
         *   <td>If the visible row count is greater than zero, the preferredHeight
         *       is the maximum cell height * visibleRowCount. If the visible row
         *       count is &lt;= 0, the preferred height is either the current height
         *       of the list, or the maximum cell height, whichever is
         *       bigger. The preferred width is than the maximum cell width *
         *       number of columns needed. Where the number of columns needs is
         *       list.height / max cell height. Max cell height is either the fixed
         *       cell height, or is determined by iterating through all the cells
         *       to find the maximum height from the ListCellRenderer.
         * <tr>
         *   <td>JList.HORIZONTAL_WRAP
         *   <td>If the visible row count is greater than zero, the preferredHeight
         *       is the maximum cell height * adjustedRowCount.  Where
         *       visibleRowCount is used to determine the number of columns.
         *       Because this lays out horizontally the number of rows is
         *       then determined from the column count.  For example, lets say
         *       you have a model with 10 items and the visible row count is 8.
         *       The number of columns needed to display this is 2, but you no
         *       longer need 8 rows to display this, you only need 5, thus
         *       the adjustedRowCount is 5.
         *       <p>If the visible row
         *       count is &lt;= 0, the preferred height is dictated by the
         *       number of columns, which will be as many as can fit in the width
         *       of the <code>JList</code> (width / max cell width), with at
         *       least one column.  The preferred height then becomes the
         *       model size / number of columns * maximum cell height.
         *       Max cell height is either the fixed
         *       cell height, or is determined by iterating through all the cells
         *       to find the maximum height from the ListCellRenderer.
         * </table>
         * The above specifies the raw preferred width and height. The resulting
         * preferred width is the above width + insets.left + insets.right and
         * the resulting preferred height is the above height + insets.top +
         * insets.bottom. Where the <code>Insets</code> are determined from
         * <code>list.getInsets()</code>.
         *
         * @param c The JList component.
         * @return The total size of the list.
         */
        public Dimension getPreferredSize(JComponent c) {
            maybeUpdateLayoutState();

            int lastRow = list.getModel().getSize() - 1;
            if (lastRow < 0) {
                return new Dimension(0, 0);
            }

            Insets insets = list.getInsets();
            int width = cellWidth * columnCount + insets.left + insets.right;
            int height;

            if (layoutOrientation != JList.VERTICAL) {
                height = preferredHeight;
            }
            else {
                Rectangle bounds = getCellBounds(list, lastRow);

                if (bounds != null) {
                    height = bounds.y + bounds.height + insets.bottom;
                }
                else {
                    height = 0;
                }
            }
            return new Dimension(width, height);
        }


        /**
         * Selected the previous row and force it to be visible.
         *
         * @see JList#ensureIndexIsVisible
         */
        protected void selectPreviousIndex() {
            int s = list.getSelectedIndex();
            if(s > 0) {
                s -= 1;
                list.setSelectedIndex(s);
                list.ensureIndexIsVisible(s);
            }
        }


        /**
         * Selected the previous row and force it to be visible.
         *
         * @see JList#ensureIndexIsVisible
         */
        protected void selectNextIndex()
        {
            int s = list.getSelectedIndex();
            if((s + 1) < list.getModel().getSize()) {
                s += 1;
                list.setSelectedIndex(s);
                list.ensureIndexIsVisible(s);
            }
        }


        /**
         * Unregisters keyboard actions installed from
         * <code>installKeyboardActions</code>.
         * This method is called at uninstallUI() time - subclassess should
         * ensure that all of the keyboard actions registered at installUI
         * time are removed here.
         *
         * @see #installUI
         */
        protected void uninstallKeyboardActions() {
            SwingUtilities.replaceUIActionMap(list, null);
            SwingUtilities.replaceUIInputMap(list, JComponent.WHEN_FOCUSED, null);
        }


        /**
         * Initializes list properties such as font, foreground, and background,
         * and adds the CellRendererPane. The font, foreground, and background
         * properties are only set if their current value is either null
         * or a UIResource, other properties are set if the current
         * value is null.
         *
         * @see #uninstallDefaults
         * @see #installUI
         * @see CellRendererPane
         */
        protected void installDefaults()
        {
            list.setLayout(null);

            LookAndFeel.installBorder(list, "List.border");

            LookAndFeel.installColorsAndFont(list, "List.background", "List.foreground", "List.font");

            LookAndFeel.installProperty(list, "opaque", Boolean.TRUE);

            if (list.getCellRenderer() == null) {
                list.setCellRenderer((ListCellRenderer)(UIManager.get("List.cellRenderer")));
            }

            Color sbg = list.getSelectionBackground();
            if (sbg == null || sbg instanceof UIResource) {
                list.setSelectionBackground(UIManager.getColor("List.selectionBackground"));
            }

            Color sfg = list.getSelectionForeground();
            if (sfg == null || sfg instanceof UIResource) {
                list.setSelectionForeground(UIManager.getColor("List.selectionForeground"));
            }

            Long l = (Long)UIManager.get("List.timeFactor");
            timeFactor = (l!=null) ? l.longValue() : 1000L;

            updateIsFileList();
        }

        private void updateIsFileList() {
            boolean b = Boolean.TRUE.equals(list.getClientProperty("List.isFileList"));
            if (b != isFileList) {
                isFileList = b;
                Font oldFont = list.getFont();
                if (oldFont == null || oldFont instanceof UIResource) {
                    Font newFont = UIManager.getFont(b ? "FileChooser.listFont" : "List.font");
                    if (newFont != null && newFont != oldFont) {
                        list.setFont(newFont);
                    }
                }
            }
        }


        /**
         * Sets the list properties that have not been explicitly overridden to
         * {@code null}. A property is considered overridden if its current value
         * is not a {@code UIResource}.
         *
         * @see #installDefaults
         * @see #uninstallUI
         * @see CellRendererPane
         */
        protected void uninstallDefaults()
        {
            LookAndFeel.uninstallBorder(list);
            if (list.getFont() instanceof UIResource) {
                list.setFont(null);
            }
            if (list.getForeground() instanceof UIResource) {
                list.setForeground(null);
            }
            if (list.getBackground() instanceof UIResource) {
                list.setBackground(null);
            }
            if (list.getSelectionBackground() instanceof UIResource) {
                list.setSelectionBackground(null);
            }
            if (list.getSelectionForeground() instanceof UIResource) {
                list.setSelectionForeground(null);
            }
            if (list.getCellRenderer() instanceof UIResource) {
                list.setCellRenderer(null);
            }
            if (list.getTransferHandler() instanceof UIResource) {
                list.setTransferHandler(null);
            }
        }


        /**
         * Initializes <code>this.list</code> by calling <code>installDefaults()</code>,
         * <code>installListeners()</code>, and <code>installKeyboardActions()</code>
         * in order.
         *
         * @see #installDefaults
         * @see #installListeners
         * @see #installKeyboardActions
         */
        public void installUI(JComponent c)
        {
            list = (JList)c;

            layoutOrientation = list.getLayoutOrientation();

            rendererPane = new CellRendererPane();
            list.add(rendererPane);

            columnCount = 1;

            updateLayoutStateNeeded = modelChanged;
            isLeftToRight = list.getComponentOrientation().isLeftToRight();

            installDefaults();
            installListeners();
            installKeyboardActions();
        }


        /**
         * Uninitializes <code>this.list</code> by calling <code>uninstallListeners()</code>,
         * <code>uninstallKeyboardActions()</code>, and <code>uninstallDefaults()</code>
         * in order.  Sets this.list to null.
         *
         * @see #uninstallListeners
         * @see #uninstallKeyboardActions
         * @see #uninstallDefaults
         */
        public void uninstallUI(JComponent c)
        {
            uninstallListeners();
            uninstallDefaults();
            uninstallKeyboardActions();

            cellWidth = cellHeight = -1;
            cellHeights = null;

            listWidth = listHeight = -1;

            list.remove(rendererPane);
            rendererPane = null;
            list = null;
        }


        /**
         * Returns a new instance of BasicListUI.  BasicListUI delegates are
         * allocated one per JList.
         *
         * @return A new ListUI implementation for the Windows look and feel.
         */
        public static ComponentUI createUI(JComponent list) {
            return new BasicListUI();
        }


        /**
         * {@inheritDoc}
         * @throws NullPointerException {@inheritDoc}
         */
        public int locationToIndex(JList list, Point location) {
            maybeUpdateLayoutState();
            return convertLocationToModel(location.x, location.y);
        }


        /**
         * {@inheritDoc}
         */
        public Point indexToLocation(JList list, int index) {
            maybeUpdateLayoutState();
            Rectangle rect = getCellBounds(list, index, index);

            if (rect != null) {
                return new Point(rect.x, rect.y);
            }
            return null;
        }


        /**
         * {@inheritDoc}
         */
        public Rectangle getCellBounds(JList list, int index1, int index2) {
            maybeUpdateLayoutState();

            int minIndex = Math.min(index1, index2);
            int maxIndex = Math.max(index1, index2);

            if (minIndex >= list.getModel().getSize()) {
                return null;
            }

            Rectangle minBounds = getCellBounds(list, minIndex);

            if (minBounds == null) {
                return null;
            }
            if (minIndex == maxIndex) {
                return minBounds;
            }
            Rectangle maxBounds = getCellBounds(list, maxIndex);

            if (maxBounds != null) {
                if (layoutOrientation == JList.HORIZONTAL_WRAP) {
                    int minRow = convertModelToRow(minIndex);
                    int maxRow = convertModelToRow(maxIndex);

                    if (minRow != maxRow) {
                        minBounds.x = 0;
                        minBounds.width = list.getWidth();
                    }
                }
                else if (minBounds.x != maxBounds.x) {
                    // Different columns
                    minBounds.y = 0;
                    minBounds.height = list.getHeight();
                }
                minBounds.add(maxBounds);
            }
            return minBounds;
        }

        /**
         * Gets the bounds of the specified model index, returning the resulting
         * bounds, or null if <code>index</code> is not valid.
         */
        private Rectangle getCellBounds(JList list, int index) {
            maybeUpdateLayoutState();

            int row = convertModelToRow(index);
            int column = convertModelToColumn(index);

            if (row == -1 || column == -1) {
                return null;
            }

            Insets insets = list.getInsets();
            int x;
            int w = cellWidth;
            int y = insets.top;
            int h;
            switch (layoutOrientation) {
            case JList.VERTICAL_WRAP:
            case JList.HORIZONTAL_WRAP:
                if (isLeftToRight) {
                    x = insets.left + column * cellWidth;
                } else {
                    x = list.getWidth() - insets.right - (column+1) * cellWidth;
                }
                y += cellHeight * row;
                h = cellHeight;
                break;
            default:
                x = insets.left;
                if (cellHeights == null) {
                    y += (cellHeight * row);
                }
                else if (row >= cellHeights.length) {
                    y = 0;
                }
                else {
                    for(int i = 0; i < row; i++) {
                        y += cellHeights[i];
                    }
                }
                w = list.getWidth() - (insets.left + insets.right);
                h = getRowHeight(index);
                break;
            }
            return new Rectangle(x, y, w, h);
        }

        /**
         * Returns the height of the specified row based on the current layout.
         *
         * @return The specified row height or -1 if row isn't valid.
         * @see #convertYToRow
         * @see #convertRowToY
         * @see #updateLayoutState
         */
        protected int getRowHeight(int row)
        {
            return getHeight(0, row);
        }


        /**
         * Convert the JList relative coordinate to the row that contains it,
         * based on the current layout.  If y0 doesn't fall within any row,
         * return -1.
         *
         * @return The row that contains y0, or -1.
         * @see #getRowHeight
         * @see #updateLayoutState
         */
        protected int convertYToRow(int y0)
        {
            return convertLocationToRow(0, y0, false);
        }


        /**
         * Return the JList relative Y coordinate of the origin of the specified
         * row or -1 if row isn't valid.
         *
         * @return The Y coordinate of the origin of row, or -1.
         * @see #getRowHeight
         * @see #updateLayoutState
         */
        protected int convertRowToY(int row)
        {
            if (row >= getRowCount(0) || row < 0) {
                return -1;
            }
            Rectangle bounds = getCellBounds(list, row, row);
            return bounds.y;
        }

        /**
         * Returns the height of the cell at the passed in location.
         */
        private int getHeight(int column, int row) {
            if (column < 0 || column > columnCount || row < 0) {
                return -1;
            }
            if (layoutOrientation != JList.VERTICAL) {
                return cellHeight;
            }
            if (row >= list.getModel().getSize()) {
                return -1;
            }
            return (cellHeights == null) ? cellHeight :
                               ((row < cellHeights.length) ? cellHeights[row] : -1);
        }

        /**
         * Returns the row at location x/y.
         *
         * @param closest If true and the location doesn't exactly match a
         *                particular location, this will return the closest row.
         */
        private int convertLocationToRow(int x, int y0, boolean closest) {
            int size = list.getModel().getSize();

            if (size <= 0) {
                return -1;
            }
            Insets insets = list.getInsets();
            if (cellHeights == null) {
                int row = (cellHeight == 0) ? 0 :
                               ((y0 - insets.top) / cellHeight);
                if (closest) {
                    if (row < 0) {
                        row = 0;
                    }
                    else if (row >= size) {
                        row = size - 1;
                    }
                }
                return row;
            }
            else if (size > cellHeights.length) {
                return -1;
            }
            else {
                int y = insets.top;
                int row = 0;

                if (closest && y0 < y) {
                    return 0;
                }
                int i;
                for (i = 0; i < size; i++) {
                    if ((y0 >= y) && (y0 < y + cellHeights[i])) {
                        return row;
                    }
                    y += cellHeights[i];
                    row += 1;
                }
                return i - 1;
            }
        }

        /**
         * Returns the closest row that starts at the specified y-location
         * in the passed in column.
         */
        private int convertLocationToRowInColumn(int y, int column) {
            int x = 0;

            if (layoutOrientation != JList.VERTICAL) {
                if (isLeftToRight) {
                    x = column * cellWidth;
                } else {
                    x = list.getWidth() - (column+1)*cellWidth - list.getInsets().right;
                }
            }
            return convertLocationToRow(x, y, true);
        }

        /**
         * Returns the closest location to the model index of the passed in
         * location.
         */
        private int convertLocationToModel(int x, int y) {
            int row = convertLocationToRow(x, y, true);
            int column = convertLocationToColumn(x, y);

            if (row >= 0 && column >= 0) {
                return getModelIndex(column, row);
            }
            return -1;
        }

        /**
         * Returns the number of rows in the given column.
         */
        private int getRowCount(int column) {
            if (column < 0 || column >= columnCount) {
                return -1;
            }
            if (layoutOrientation == JList.VERTICAL ||
                      (column == 0 && columnCount == 1)) {
                return list.getModel().getSize();
            }
            if (column >= columnCount) {
                return -1;
            }
            if (layoutOrientation == JList.VERTICAL_WRAP) {
                if (column < (columnCount - 1)) {
                    return rowsPerColumn;
                }
                return list.getModel().getSize() - (columnCount - 1) *
                            rowsPerColumn;
            }
            // JList.HORIZONTAL_WRAP
            int diff = columnCount - (columnCount * rowsPerColumn -
                                      list.getModel().getSize());

            if (column >= diff) {
                return Math.max(0, rowsPerColumn - 1);
            }
            return rowsPerColumn;
        }

        /**
         * Returns the model index for the specified display location.
         * If <code>column</code>x<code>row</code> is beyond the length of the
         * model, this will return the model size - 1.
         */
        private int getModelIndex(int column, int row) {
            switch (layoutOrientation) {
            case JList.VERTICAL_WRAP:
                return Math.min(list.getModel().getSize() - 1, rowsPerColumn *
                                column + Math.min(row, rowsPerColumn-1));
            case JList.HORIZONTAL_WRAP:
                return Math.min(list.getModel().getSize() - 1, row * columnCount +
                                column);
            default:
                return row;
            }
        }

        /**
         * Returns the closest column to the passed in location.
         */
        private int convertLocationToColumn(int x, int y) {
            if (cellWidth > 0) {
                if (layoutOrientation == JList.VERTICAL) {
                    return 0;
                }
                Insets insets = list.getInsets();
                int col;
                if (isLeftToRight) {
                    col = (x - insets.left) / cellWidth;
                } else {
                    col = (list.getWidth() - x - insets.right - 1) / cellWidth;
                }
                if (col < 0) {
                    return 0;
                }
                else if (col >= columnCount) {
                    return columnCount - 1;
                }
                return col;
            }
            return 0;
        }

        /**
         * Returns the row that the model index <code>index</code> will be
         * displayed in..
         */
        private int convertModelToRow(int index) {
            int size = list.getModel().getSize();

            if ((index < 0) || (index >= size)) {
                return -1;
            }

            if (layoutOrientation != JList.VERTICAL && columnCount > 1 &&
                                                       rowsPerColumn > 0) {
                if (layoutOrientation == JList.VERTICAL_WRAP) {
                    return index % rowsPerColumn;
                }
                return index / columnCount;
            }
            return index;
        }

        /**
         * Returns the column that the model index <code>index</code> will be
         * displayed in.
         */
        private int convertModelToColumn(int index) {
            int size = list.getModel().getSize();

            if ((index < 0) || (index >= size)) {
                return -1;
            }

            if (layoutOrientation != JList.VERTICAL && rowsPerColumn > 0 &&
                                                       columnCount > 1) {
                if (layoutOrientation == JList.VERTICAL_WRAP) {
                    return index / rowsPerColumn;
                }
                return index % columnCount;
            }
            return 0;
        }

        /**
         * If updateLayoutStateNeeded is non zero, call updateLayoutState() and reset
         * updateLayoutStateNeeded.  This method should be called by methods
         * before doing any computation based on the geometry of the list.
         * For example it's the first call in paint() and getPreferredSize().
         *
         * @see #updateLayoutState
         */
        protected void maybeUpdateLayoutState()
        {
            if (updateLayoutStateNeeded != 0) {
                updateLayoutState();
                updateLayoutStateNeeded = 0;
            }
        }


        /**
         * Recompute the value of cellHeight or cellHeights based
         * and cellWidth, based on the current font and the current
         * values of fixedCellWidth, fixedCellHeight, and prototypeCellValue.
         *
         * @see #maybeUpdateLayoutState
         */
        protected void updateLayoutState()
        {
            /* If both JList fixedCellWidth and fixedCellHeight have been
             * set, then initialize cellWidth and cellHeight, and set
             * cellHeights to null.
             */

            int fixedCellHeight = list.getFixedCellHeight();
            int fixedCellWidth = list.getFixedCellWidth();

            cellWidth = (fixedCellWidth != -1) ? fixedCellWidth : -1;

            if (fixedCellHeight != -1) {
                cellHeight = fixedCellHeight;
                cellHeights = null;
            }
            else {
                cellHeight = -1;
                cellHeights = new int[list.getModel().getSize()];
            }

            /* If either of  JList fixedCellWidth and fixedCellHeight haven't
             * been set, then initialize cellWidth and cellHeights by
             * scanning through the entire model.  Note: if the renderer is
             * null, we just set cellWidth and cellHeights[*] to zero,
             * if they're not set already.
             */

            if ((fixedCellWidth == -1) || (fixedCellHeight == -1)) {

                ListModel dataModel = list.getModel();
                int dataModelSize = dataModel.getSize();
                ListCellRenderer renderer = list.getCellRenderer();

                if (renderer != null) {
                    for(int index = 0; index < dataModelSize; index++) {
                        Object value = dataModel.getElementAt(index);
                        Component c = renderer.getListCellRendererComponent(list, value, index, false, false);
                        rendererPane.add(c);
                        Dimension cellSize = c.getPreferredSize();
                        if (fixedCellWidth == -1) {
                            cellWidth = Math.max(cellSize.width, cellWidth);
                        }
                        if (fixedCellHeight == -1) {
                            cellHeights[index] = cellSize.height;
                        }
                    }
                }
                else {
                    if (cellWidth == -1) {
                        cellWidth = 0;
                    }
                    if (cellHeights == null) {
                        cellHeights = new int[dataModelSize];
                    }
                    for(int index = 0; index < dataModelSize; index++) {
                        cellHeights[index] = 0;
                    }
                }
            }

            columnCount = 1;
            if (layoutOrientation != JList.VERTICAL) {
                updateHorizontalLayoutState(fixedCellWidth, fixedCellHeight);
            }
        }

        /**
         * Invoked when the list is layed out horizontally to determine how
         * many columns to create.
         * <p>
         * This updates the <code>rowsPerColumn, </code><code>columnCount</code>,
         * <code>preferredHeight</code> and potentially <code>cellHeight</code>
         * instance variables.
         */
        private void updateHorizontalLayoutState(int fixedCellWidth,
                                                 int fixedCellHeight) {
            int visRows = list.getVisibleRowCount();
            int dataModelSize = list.getModel().getSize();
            Insets insets = list.getInsets();

            listHeight = list.getHeight();
            listWidth = list.getWidth();

            if (dataModelSize == 0) {
                rowsPerColumn = columnCount = 0;
                preferredHeight = insets.top + insets.bottom;
                return;
            }

            int height;

            if (fixedCellHeight != -1) {
                height = fixedCellHeight;
            }
            else {
                // Determine the max of the renderer heights.
                int maxHeight = 0;
                if (cellHeights.length > 0) {
                    maxHeight = cellHeights[cellHeights.length - 1];
                    for (int counter = cellHeights.length - 2;
                         counter >= 0; counter--) {
                        maxHeight = Math.max(maxHeight, cellHeights[counter]);
                    }
                }
                height = cellHeight = maxHeight;
                cellHeights = null;
            }
            // The number of rows is either determined by the visible row
            // count, or by the height of the list.
            rowsPerColumn = dataModelSize;
            if (visRows > 0) {
                rowsPerColumn = visRows;
                columnCount = Math.max(1, dataModelSize / rowsPerColumn);
                if (dataModelSize > 0 && dataModelSize > rowsPerColumn &&
                    dataModelSize % rowsPerColumn != 0) {
                    columnCount++;
                }
                if (layoutOrientation == JList.HORIZONTAL_WRAP) {
                    // Because HORIZONTAL_WRAP flows differently, the
                    // rowsPerColumn needs to be adjusted.
                    rowsPerColumn = (dataModelSize / columnCount);
                    if (dataModelSize % columnCount > 0) {
                        rowsPerColumn++;
                    }
                }
            }
            else if (layoutOrientation == JList.VERTICAL_WRAP && height != 0) {
                rowsPerColumn = Math.max(1, (listHeight - insets.top -
                                             insets.bottom) / height);
                columnCount = Math.max(1, dataModelSize / rowsPerColumn);
                if (dataModelSize > 0 && dataModelSize > rowsPerColumn &&
                    dataModelSize % rowsPerColumn != 0) {
                    columnCount++;
                }
            }
            else if (layoutOrientation == JList.HORIZONTAL_WRAP && cellWidth > 0 &&
                     listWidth > 0) {
                columnCount = Math.max(1, (listWidth - insets.left -
                                           insets.right) / cellWidth);
                rowsPerColumn = dataModelSize / columnCount;
                if (dataModelSize % columnCount > 0) {
                    rowsPerColumn++;
                }
            }
            preferredHeight = rowsPerColumn * cellHeight + insets.top +
                                  insets.bottom;
        }



        private void redrawList() {
            list.revalidate();
            list.repaint();
        }


        /** Used by IncrementLeadSelectionAction. Indicates the action should
         * change the lead, and not select it. */
        private static final int CHANGE_LEAD = 0;
        /** Used by IncrementLeadSelectionAction. Indicates the action should
         * change the selection and lead. */
        private static final int CHANGE_SELECTION = 1;
        /** Used by IncrementLeadSelectionAction. Indicates the action should
         * extend the selection from the anchor to the next index. */
        private static final int EXTEND_SELECTION = 2;


        private static int adjustIndex(int index, JList list) {
            return index < list.getModel().getSize() ? index : -1;
        }



}
