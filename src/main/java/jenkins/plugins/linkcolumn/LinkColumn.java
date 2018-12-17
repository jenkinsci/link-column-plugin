/*
 * The MIT License
 *
 * Copyright (c) 2018 - present, Karl Gong
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
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package jenkins.plugins.linkcolumn;

import hudson.EnvVars;
import hudson.model.Result;
import jenkins.model.Jenkins;
import org.kohsuke.stapler.DataBoundConstructor;

import hudson.Extension;
import hudson.model.Items;
import hudson.model.Job;
import hudson.model.Run;
import hudson.views.ListViewColumn;
import hudson.views.ListViewColumnDescriptor;

public class LinkColumn extends ListViewColumn {

    private String columnName;
    private String linkText;
    private String linkUrl;
    private boolean openNewWindow;

    @DataBoundConstructor
    public LinkColumn(String columnName, String linkText, String linkUrl, boolean openNewWindow) {
        super();
        this.columnName = columnName;
        this.linkText = linkText;
        this.linkUrl = linkUrl;
        this.openNewWindow = openNewWindow;
    }


    public String getColumnName() {
        return columnName;
    }

    public String getLinkText() {
        return linkText;
    }

    public String getLinkUrl() {
        return linkUrl;
    }

    public boolean isOpenNewWindow() {
        return openNewWindow;
    }

    public String getTokenizedLinkText(Job<?, ?> job) {
        return tokenize(linkText, job);
    }

    public String getTokenizedLinkUrl(Job<?, ?> job) {
        return tokenize(linkUrl, job);
    }

    public String getLinkTarget() {
        return this.openNewWindow ? "_blank" : "_self";
    }

    private String tokenize(String value, Job<?, ?> job) {
        String rootUrl = "null";
        String jobName = "null";
        String jobUrl = "null";
        String buildNumber = "null";
        String buildUrl = "null";
        String buildStatus = "null";

        Jenkins instance = Jenkins.getInstance();
        if (instance != null) {
            rootUrl = instance.getRootUrl();
            jobName = job.getName();
            jobUrl = rootUrl + job.getShortUrl();

            Run<?, ?> lastBuild = job.getLastBuild();
            if (lastBuild != null) {
                buildNumber = String.valueOf(lastBuild.getNumber());
                buildUrl = jobUrl + buildNumber + "/";
                
                Result result = lastBuild.getResult();
                if (result != null) {
                    buildStatus = result.toString();
                }
            }
        }

        EnvVars envVars = new EnvVars();
        envVars.put("ROOT_URL", rootUrl);
        envVars.put("JOB_NAME", jobName);
        envVars.put("JOB_URL", jobUrl);
        envVars.put("BUILD_NUMBER", buildNumber);
        envVars.put("BUILD_URL", buildUrl);
        envVars.put("BUILD_STATUS", buildStatus);

        return envVars.expand(value);
    }

    @Extension
    public static class DescriptorImpl extends ListViewColumnDescriptor {

        public DescriptorImpl() {
            Items.XSTREAM2.addCompatibilityAlias("hudson.views.LinkColumn", LinkColumn.class);
        }

        @Override
        public boolean shownByDefault() {
            return false;
        }

        @Override
        public String getDisplayName() {
            return "Custom Link";
        }

        @Override
        public String getHelpFile() {
            return "/plugin/link-column/link-column.html";
        }
    }

}
