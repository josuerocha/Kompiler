/*
 * The MIT License
 *
 * Copyright 2017 jr.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package dataunits;

/**
 *
 * @author jr
 */
public enum Tag {
    
    /**
 * List of tokens and their corresponding code. 
 * 
 * <p>
 * **/
    
    //RESERVED WORDS
    FOR(256), 
    WHILE(257), 
    STATIC(258), 
    PRIVATE(259),
    PUBLIC(260),
    
    //RELATIONAL OPERATORS
    LESS(300), 
    GREATER(301), 
    LESS_EQUAL(302), 
    GREATER_EQUAL(303),
    
    //MATHEMATICAL OPERATORS
    ASSIGN(350), 
    PLUS(351), 
    MINUS(352), 
    TIMES(353), 
    DIVIDE(354);
    
    
    /**
    * Tag value container.
    */ 
    private final int tagValue;
    
    /**
     * Constructor of the Tag enumerated.
     * @param tagValue : input argument to pass respective tag value.
     */
    Tag(int tagValue){
        this.tagValue = tagValue;
    }
    
}
