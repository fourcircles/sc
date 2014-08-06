package kz.arta.synergy.components.client.input;

import com.google.gwt.event.dom.client.*;

import java.util.HashMap;
import java.util.Map;

/**
 * User: user
 * Date: 30.07.14
 * Time: 17:53
 * Компонент ввода текста с маской ввода
 */
public class MaskTextInput extends TextInput implements BlurHandler, FocusHandler, KeyDownHandler, KeyPressHandler {

    public class Settings {

        private String placeHolder;

        public Settings() {

        }

        public Settings(String placeHolder) {
            this.placeHolder = placeHolder;
        }

        public String getPlaceHolder() {
            return placeHolder;
        }

        public void setPlaceHolder(String placeHolder) {
            this.placeHolder = placeHolder;
        }

    }

    private final String mask;

    private int len;

    private Settings settings;
    private int partialPosition;
    private String[] buffer;
    private boolean ignore;
    private String focusText;
    private String[] tests;

    private Integer firstNonMaskPos;
    private int cursorBegin = -1;

    private int cursorEnd = -1;

    private static final Map<String, String> defs;

    static {

        defs = new HashMap<String, String>();
        defs.put("#", "[0-9]");
        defs.put("a", "[a-z]");
        defs.put("A", "[A-Z]");
        defs.put("*", "[A-Za-z0-9]");

    }

    private static boolean containDef(String key) {
        return defs.get(key) != null;
    }

    private static String getDef(String key) {
        return defs.get(key);
    }

    public MaskTextInput(String mask) {
        this.mask = mask;
        this.addBlurHandler(this);
        this.addFocusHandler(this);
        this.addKeyDownHandler(this);
        this.addKeyPressHandler(this);
        maskField();
        setMaxLength(len);
    }

    private void buffer() {

        String[] aux = split(mask);

        buffer = new String[aux.length];

        for (int i = 0; i < aux.length; i++) {

            if (containDef(aux[i])) {
                buffer[i] = settings.getPlaceHolder();
            } else {
                buffer[i] = aux[i];
            }

        }

    }

    private int checkVal(boolean allow) {

        String test = "";

        if (getValue() != null) {
            test = getValue();
        }

        int lastMatch = -1;

        int a = 0;

        for (int i = 0, pos = 0; i < len; i++) {

            if (tests[i] != null) {

                buffer[i] = settings.getPlaceHolder();

                while (pos++ < test.length()) {

                    String c = String.valueOf(test.charAt(pos - 1));

                    if (c.matches(tests[i])) {

                        buffer[i] = String.valueOf(c);
                        lastMatch = i;
                        break;
                    }
                }
                if (pos > test.length()) {
                    break;
                }
            } else if (i != partialPosition) {

                try {

                    char d = test.charAt(pos);

                    if (buffer[i].equals(String.valueOf(d))) {

                        pos++;
                        lastMatch = i;
                    }

                } catch (Exception e) {
                    continue;
                }
            }

            a = i;
        }

        if (!allow && lastMatch + 1 < partialPosition) {

            setText("");
            clearBuffer(0, len);

        } else if (allow || lastMatch + 1 >= partialPosition) {

            writeBuffer();

            if (!allow) {

                if (getValue() != null) {
                    setText(getValue().substring(0, lastMatch + 1));
                }
            }
        }

        return a;

    }

    private void clearBuffer(int start, int end) {

        for (int i = start; i < end && i < len; i++) {
            if (tests[i] != null) {
                buffer[i] = settings.getPlaceHolder();
            }
        }

    }

    private void each() {

        for (int i = 0; i < tests.length; i++) {

            String c = tests[i];

            if (c.equals("?")) {

                len--;
                partialPosition = i;

            } else if (containDef(c)) {

                tests[i] = getDef(c);

                if (firstNonMaskPos == null) {
                    firstNonMaskPos = tests.length - 1;
                }

            } else {
                tests[i] = null;
            }

        }

    }

    private void maskField() {

        settings = new Settings("_");

        tests = new String[]{};
        partialPosition = mask.length();
        firstNonMaskPos = null;
        len = mask.length();

        tests = split(mask);

        each();
        buffer();

        ignore = false;

        focusText = "";

        if (getValue() != null) {
            focusText = getValue();
        }

        if (!isReadOnly()) {

            checkVal(false);
        }

    }


    public void onBlur(BlurEvent be) {
        checkVal(false);
    }

    public void onFocus(FocusEvent be) {

        focusText = "";

        if (getValue() != null) {
            focusText = getValue();
        }

        int pos = checkVal(false);
        writeBuffer();

        if (pos == mask.length()) {
            cursorBegin = 0;
            cursorEnd = pos;

            setCursorPos(0);
        } else {

            cursorBegin = pos;
            cursorEnd = pos;

            setCursorPos(pos);
        }
        setCursorPos(0);
    }

    @Override
    public void onKeyDown(KeyDownEvent fe) {

        int k = fe.getNativeKeyCode();

        ignore = k < 16 || k > 16 && k < 32 || k > 32 && k < 41;

        // delete selection before proceeding
        if (cursorBegin - cursorEnd != 0 && (!ignore || k == KeyCodes.KEY_BACKSPACE || k == KeyCodes.KEY_DELETE)) {
            clearBuffer(cursorBegin, cursorEnd);
        }

        // backspace, delete, and escape get special treatment
        if (k == KeyCodes.KEY_BACKSPACE || k == KeyCodes.KEY_DELETE) {

            shiftL(getCursorPos() + (k == KeyCodes.KEY_DELETE ? 0 : -1));
            cancelKey();
        } else if (k == KeyCodes.KEY_ESCAPE) {// escape
            setText(focusText);
            cancelKey();
        }
    }

    @Override
    public void onKeyPress(KeyPressEvent fe) {

        try {
            int k = fe.getCharCode();

            if (ignore) {
                // Fixes Mac FF bug on backspace

                if (k == KeyCodes.KEY_BACKSPACE) {
                    cancelKey();
                }

                return;
            }

            if (fe.isControlKeyDown() || fe.isAltKeyDown()) {// Ignore

                cancelKey();

            } else if (k >= 32 && k <= 125 || k > 186) {// typeable characters

                int p = seekNext(getCursorPos() - 1);

                if (p < len) {

                    String c = String.valueOf(fe.getCharCode());

                    if (c.matches(tests[p])) {

                        shiftR(p);
                        buffer[p] = c;
                        writeBuffer();
                        int next = seekNext(p);

                        setCursorPos(next);

                        cursorBegin = next;
                        cursorEnd = next;
                    }
                }
            }

            cancelKey();
        } catch (Exception ignore) {

        }
    }

    private int seekNext(int index) {

        while (++index <= len) {

            try {
                if (tests[index] != null) {
                    break;
                }
            } catch (Exception e) {
                break;
            }
        }

        return index;

    }

    private void shiftL(int index) {

        if (index < 0) {
            return;
        }

        for (int i = index; i >= 0; i--) {

            if (tests[i] != null) {

                index = i;
                break;
            }
        }

        for (int i = index; i < len; i++) {

            if (tests[i] != null) {

                buffer[i] = settings.getPlaceHolder();

                int j = seekNext(i);

                if (j < len && buffer[j].matches(tests[i])) {
                    buffer[i] = buffer[j];
                } else {
                    break;
                }
            }
        }

        writeBuffer();

        setCursorPos(index);

    }

    private void shiftR(int index) {

        String c = settings.getPlaceHolder();

        if (tests[index] != null) {
            buffer[index] = c;
        }

    }

    private String[] split(String text) {

        int length = text.length();

        String[] array = new String[length];

        for (int i = 0; i < length; i++) {
            array[i] = String.valueOf(text.charAt(i));
        }

        return array;
    }

    private void writeBuffer() {

        String valueAux = "";

        for (String element2 : buffer) {

            valueAux += element2;
        }

        setText(valueAux);

    }
}