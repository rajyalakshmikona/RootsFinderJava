package com.korsak.rootsfinder;

import java.util.*;

import static java.lang.Math.*;


/**
 * It`s purpose is to find roots of polynomial equations
 */
class CalculateRoots {

    /**
     * @param factors a list with our factors
     * @return list of roots
     */
    List<Complex> getRoots(List<Double> factors) {
        int degree = factors.size() - 1;
        List<Complex> roots = new ArrayList<>();
        switch (degree){
            case 0:
                return new ArrayList<>();
            case 1:
                return new ArrayList<>(Collections.singletonList(linear(factors)));
            case 2:
                return quadratic(factors);
        }

        int rootsFound = 0;
        Double value, temp;
        Complex root = new Complex();

        // check for roots in integers [-1000,1000]
        for (Double i = -500.0; i <= 500.0; i += 0.01) {
            value = evaluate(factors, i);
            if (value == 0.0) {
                rootsFound++;
                root.setRealPart(setPrecision(i));
                roots.add(root);
                if(root.getImaginaryPart() == 0.0) {
                    factors = hornersMethod(factors, root);
                }
                root = new Complex();
                i--;
            }

            temp = evaluate(factors, i + 0.01);
            if (temp * value < 0) {
                // a  is somewhere in here
                root.setRealPart(setPrecision(newtonsMethod(factors, rootsFound, degree, i)));
                roots.add(root);
                /*
                extend horners to  numbers
                 */
                if(root.getImaginaryPart() == 0.0 ) {
                    factors = hornersMethod(factors, root);
                }
                root = new Complex();
                i--;

                if (roots.size() == degree) return roots;
            }
        }
        return roots;
    }

    private Complex linear(List<Double> factors) {
        return new Complex(((-factors.get(0)) / (factors.get(1))), 0.0);
    }

    private List<Complex> quadratic(List<Double> factors) {
        Complex complex_1 = new Complex();
        Complex complex_2 = new Complex();
        Double x_1;
        Double x_2;
        Double y_1;
        Double y_2;

        Double delta = pow(factors.get(1), 2) - 4 * factors.get(0) * factors.get(2);
        Double denominator = 2 * factors.get(2);
        if(delta >= 0) {
            x_1 = ((-factors.get(1) - sqrt(delta)) /  denominator);
            x_2 = ((-factors.get(1) + sqrt(delta)) / denominator);

            complex_1.setRealPart(x_1);
            complex_2.setRealPart(x_2);
        } else {
            delta = - delta;
            x_1 = ((-factors.get(1))/denominator);
            x_2 = x_1;
            complex_1.setRealPart(x_1);
            complex_2.setRealPart(x_2);
            y_1 = (-sqrt(delta)/denominator);
            y_2 = -y_1;
            complex_1.setImaginaryPart(y_1);
            complex_2.setImaginaryPart(y_2);
        }

        return new ArrayList<>(Arrays.asList(complex_1, complex_2));
    }


    private Double newtonsMethod(List<Double> factors, int rootsFound, int degree, Double argument) {
        Double result, offset, temp;
        boolean change = true;
        offset = 0.005;
        while (true) {
            do {
                result = evaluate(factors, argument);

                if ((abs(result) == 0.0)) {
                    rootsFound++;
                }

                if (rootsFound == degree) return argument;

                if (change) argument += offset;
                else argument -= offset;

                temp = evaluate(factors, argument);

            } while (abs(result) > abs(temp));

            offset /= 2;

            if (offset < pow(10, -8)) {
                return argument;
            }
            change = !change;
        }
    }


    /**
     * It executes the horners method on a list of Doubles
     * TODO: extend it to complex numbers
     *
     * @param factors input list
     * @param root    input root
     * @return result of horner's method
     */
    private List<Complex> hornersMethod(List<Complex> factors, Complex root) {
        int i = factors.size() - 1;
        List<Complex> resultOfDivision = new ArrayList<>();
        Complex temp = new Complex();

        resultOfDivision.add(factors.get(i));

        temp.multiply(Arrays.asList(root, factors.get(i)))
                .add(Collections.singletonList(factors.get(i - 1)));

        resultOfDivision.add(temp);


        while (i > 1) {
            i--;
            temp.multiply(Arrays.asList(root, temp)).add(Collections.singletonList(factors.get(i - 1)));
            resultOfDivision.add(temp);
        }

        // change order of resultOfDivision
        List<Complex> result = new ArrayList<>();
        for (int j = resultOfDivision.size() - 1; j >= 0; j--) {
            result.add(resultOfDivision.get(j));
        }
        result.remove(0);
        return result;
    }

    /**
     * Evaluate the equation with a for loop
     *
     * @param factors  a list with our factors
     * @param argument of the function
     * @return value of the function
     */
    private Double evaluate(List<Double> factors, Double argument) {

        Double value = 0.0;
        int degree = factors.size() - 1;

        for (int i = 0; i <= degree; i++) {
            value += factors.get(i) * pow(argument, i);
        }
        return value;
    }

    /**
     * Round up Doubles to 3 decimal places
     *
     * @param argument what needs to be rounded up
     * @return the result
     */
    private Double setPrecision(Double argument) {
        return Double.parseDouble(String.format(Locale.US, "%.3f", argument));
    }
}
